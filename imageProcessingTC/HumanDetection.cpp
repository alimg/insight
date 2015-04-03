#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <iostream>
#include <stdio.h>
#include <raspicam/raspicam_cv.h>

using namespace std;
using namespace cv;

void detectAndDisplay(Mat frame);

string face_cascade_name = "haarcascade_frontalface_default.xml";
CascadeClassifier face_cascade;

string eye_cascade_name = "haarcascade_eye.xml";
CascadeClassifier eye_cascade;

int filenumber; // Number of file to be saved
string filename;

void faceDetection(Mat frame)
{
    std::vector<Rect> faces;
    Mat frame_gray;
    Mat crop;
    Mat res;
    Mat gray;
        Mat original = frame.clone();

    cvtColor(frame, frame_gray, COLOR_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    // Detect faces
    face_cascade.detectMultiScale(frame_gray, faces, 1.2, 5, 0 | CASCADE_SCALE_IMAGE, Size(30, 30));

    // Set Region of Interest
    cv::Rect roi_b;
    cv::Rect roi_c;

    size_t ic = 0; // ic is index of current element
    int ac = 0; // ac is area of current element
    size_t ib = 0; // ib is index of biggest element
    int ab = 0; // ab is area of biggest element
    int counter = 0; // counter is index of consequent frames with faces

    for (ic = 0; ic < faces.size(); ++ic){ // Iterate through all current elements (detected faces)
        roi_c.x = faces[ic].x;
        roi_c.y = faces[ic].y;
        roi_c.width = (faces[ic].width);
        roi_c.height = (faces[ic].height);

	Mat faceROI = frame_gray( faces[ic] );
        //std::vector<Rect> eyes;
	//eye_cascade.detectMultiScale( faceROI, eyes, 1.1, 2, 0 |CV_HAAR_SCALE_IMAGE, Size(30, 30) );

        crop = frame(roi_c);
        resize(crop, res, Size(128, 128), 0, 0, INTER_LINEAR); // This will be needed later while saving images
        //cvtColor(crop, gray, CV_BGR2GRAY); // Convert cropped image to Grayscale

        // Form a filename
        filename = "";
        stringstream ssfn;
        ssfn << filenumber << ".png";
        filename = ssfn.str();
        filenumber++;

        imwrite(filename, crop);
       // if(eyes.size() != 0){
            Point pt1(faces[ic].x, faces[ic].y); // Display detected faces on main window - live stream from camera
            Point pt2((faces[ic].x + faces[ic].height), (faces[ic].y + faces[ic].width));
            rectangle(frame, pt1, pt2, Scalar(0, 255, 0), 2, 8, 0);
            printf("%zu human faces detected!\n",faces.size());
       // }
    }
}


void bodyDetection(Mat frame){

   Mat frame_gray;
   Mat crop2;
   Mat res2;
   Mat gray;
   Mat img;
   cv::Rect roi_b;
   cv::Rect roi_c;
   
   HOGDescriptor hog;
   hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());
   vector<Rect> found, found_filtered;
   hog.detectMultiScale(frame, found, 0, Size(8,8), Size(32,32), 1.05, 2);

   size_t ic,i,j;
   for (ic = 0; ic < found.size(); ++ic){ // Iterate through all current elements (detected faces)
	    Rect r = found[ic];
            //r.x += cvRound(r.width*0.1);
             double w = cvRound(r.width*0.8);
             double h = cvRound(r.height*0.9);
	    r.width = h;
	    //r.y += cvRound(r.height*0.06);
	    r.height = w;
  
	if(found.size()!= 0)
            printf("%zu Human body detected!\n",found.size());


       // crop2 = frame(r);
       // resize(crop2, res2, Size(128, 128), 0, 0, INTER_LINEAR); // This will be needed later while saving images
        //cvtColor(crop, gray, CV_BGR2GRAY); // Convert cropped image to Grayscale

        // Form a filename
       /* filename = "";
        stringstream ssfn;
        ssfn << filenumber << "-body.png";
        filename = ssfn.str();
        filenumber++;

        imwrite(filename, crop2);
         */
            Point pt1(r.x, r.y); // Display detected faces on main window - live stream from camera
            Point pt2((r.x + r.height), (r.y + r.width));
            rectangle(frame, pt1, pt2, Scalar(0, 255, 0), 2, 8, 0);
    }
}



int main(void){
    CvCapture *capture;
    Mat frame;
    if( !face_cascade.load( face_cascade_name ) ){ printf("Error loading face haarcascade file.\n"); return -1; }; 
    if( !eye_cascade.load( eye_cascade_name ) ){ printf("Error loading eye haarcascade file.\n"); return -1; };

    raspicam::RaspiCam_Cv Camera; //Cmaera object
    cv::Mat image;
    //Open camera 
    cout<<"Opening Camera..."<<endl;
    if ( !Camera.open()) {cerr<<"Error opening camera"<<endl;return -1;}
    Camera.set(CV_CAP_PROP_FRAME_WIDTH, 1024);
    Camera.set(CV_CAP_PROP_FRAME_HEIGHT, 768);
    if( capture ){
        while( true ){
            printf("frame\n");
            Camera.grab();
            Camera.retrieve ( frame);
            //frame = cvQueryFrame( capture );
            //frame = capture->read();
            printf("got frame\n");
            if( !frame.empty() ){  
                //faceDetection( frame );
                bodyDetection( frame );
            }
            else{ 
                printf("No captured frame -- Break!"); break; 
            }
            printf("end frame\n");
            // Show image
            //imshow("original", frame);
            // And display it:
            //char key = (char) waitKey(20);
            // Exit this loop on escape:
//             if(key == 27)
//                 break;
        }
    }
    return 0;
}


