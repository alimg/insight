#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <iostream>
#include <stdio.h>

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
    if (faces.size()>0)
        printf("face %zu\n",faces.size());
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

    if(found.size()!= 0)
        printf("human %zu\n",found.size());
}



int main(void){
    Mat frame;
    if( !face_cascade.load( face_cascade_name ) ){ fprintf(stderr, "Error loading face haarcascade file.\n"); return -1; }; 
    if( !eye_cascade.load( eye_cascade_name ) ){ fprintf(stderr, "Error loading eye haarcascade file.\n"); return -1; };

    cv::Mat image;
    while( true ){
            string filename;
            cin >> filename;
            frame = cv::imread(filename);
            if( !frame.empty() ){  
                faceDetection( frame );
                bodyDetection( frame );
            }
            else{ 
                printf("No captured frame -- Break!"); break; 
            }
    }
    return 0;
}


