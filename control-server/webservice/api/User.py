from contextlib import closing
from flask.ext import restful
from flask import request

import DBUtil
import ServerConstants
import SessionUtil


class Login(restful.Resource):

    def post(self):
        name = request.form['name']
        password = request.form['password']
        password = DBUtil.hash_string(name + password)
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                cursor.execute('SELECT id, name, email FROM users WHERE name=\'{}\' and password=\'{}\''.format(name, password))

                user = cursor.fetchone()

                if not user:
                    return {'status': ServerConstants.STATUS_ERROR}
                return {'status': ServerConstants.STATUS_SUCCESS,
                        'user': {'id': user[0], 'name': user[1], 'email': user[2]},
                        'session_token': SessionUtil.generate_token(name)}


class RegisterUser(restful.Resource):

    def post(self):
        name = request.form['name']
        password = request.form['password']
        email = request.form['email']
        password = DBUtil.hash_string(name + password)

        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                sql = 'INSERT INTO `users` (`id`, `name`, `email`, `password`, `push_token`) ' \
                      'VALUES (NULL, \'{}\', \'{}\', \'{}\', \'\')'.format(name, email, password)
                print sql
                cursor.execute(sql)
                db.commit()

        return {'status': '0'}


class ListInsight(restful.Resource):

    def post(self):
        email = request.form['email']
#        if len(email) < 6:
 #           return {'status': ServerConstants.STATUS_ERROR, 'message': 'mail too short'}

        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                sql = "SELECT DISTINCT(id) FROM `users` WHERE email='{}'".format(email)
                print sql
                cursor.execute(sql)
                user = cursor.fetchone()
                if user is None or len(user) < 1:
                    return {'status': ServerConstants.STATUS_ERROR, 'message': 'user not found'}
                sql = "SELECT id FROM `device` WHERE userid='{}'".format(user[0])
                print sql
                cursor.execute(sql)
                return {'status': '0', 'insight_list': cursor.fetchall()}

