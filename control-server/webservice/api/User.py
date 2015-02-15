from contextlib import closing
from flask.ext import restful
from flask import request

import DBUtil
import ServerConstants
import SessionUtil


class Login(restful.Resource):

    def post(self):
        print '\n'.join([x for x in request.form])
        if 'session' in request.form:
            #check session
            if SessionUtil.is_valid(request.form['session']):
                return {'status': ServerConstants.STATUS_SUCCESS}
            return {'status': ServerConstants.STATUS_INVALID_SESSION}
        name = request.form['name']
        print name
        password = request.form['password']
        print (name, password)
        password = DBUtil.hash_string(name + password)
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                cursor.execute('SELECT id, name, email FROM users WHERE name=\'{}\' and password=\'{}\''.format(name, password))
                user = cursor.fetchall()
                if not user:
                    return {'status': ServerConstants.STATUS_ERROR}
                user = user[0]
                return {'status': ServerConstants.STATUS_SUCCESS,
                        'user': {'id': user[0], 'name': user[1], 'email': user[2]},
                        'session_token': SessionUtil.generate_token(user[0])}


class RegisterUser(restful.Resource):

    def post(self):
        name = request.form['name']
        password = request.form['password']
        email = request.form['email']
        password = DBUtil.hash_string(name + password)

        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                sql = 'INSERT INTO `users` (`id`, `name`, `email`, `password`, `push_token`) ' \
                      'VALUES (NULL, \'{}\', \'{}\', \'{}\', \'{}\')'.format(name, email, password, "")
                cursor.execute(sql)
                db.commit()

        return {'status': '0'}


class ListInsight(restful.Resource):

    def post(self):
        user = SessionUtil.get_user_id(request.form['session'])
        if not user:
            return {'status': ServerConstants.STATUS_INVALID_SESSION}

        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                sql = "SELECT id FROM `device` WHERE userid='{}'".format(user)
                cursor.execute(sql)
                return {'status': '0', 'devices': cursor.fetchall()}

