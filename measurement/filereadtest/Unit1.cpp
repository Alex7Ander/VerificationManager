//---------------------------------------------------------------------------

#include <vcl.h>
#include <iostream.h>
#include <fstream.h>
#include <vector.h>
#include <string.h>
#pragma hdrstop

//---------------------------------------------------------------------------

#pragma argsused

using namespace std;

class MEASURMENT_RESULTS
{
private:
        String dateOfMeasurment;
        String elementType;
        String serNumberOfElement;
        bool suitability;
        int countOfFreq;

public:
        MEASURMENT_RESULTS(int cFreq);
        ~MEASURMENT_RESULTS();
        String getDateOfMeasurment(){return dateOfMeasurment;}
        String getElementType(){return elementType;}
        String getSerNumberOfElement(){return serNumberOfElement;}

        void setElementType(String type){elementType = type;}
        void setSerNumberOfElement(String serN){serNumberOfElement = serN;}

        bool getSuitability(){return suitability;}
        int getCountOfFreq(){return countOfFreq;}
        double *freq;

        double *m_s11;
        double *m_s12;
        double *m_s21;
        double *m_s22;

        double *error_m_s11;
        double *error_m_s12;
        double *error_m_s21;
        double *error_m_s22;

        double *p_s11;
        double *p_s12;
        double *p_s21;
        double *p_s22;

        double *error_p_s11;
        double *error_p_s12;
        double *error_p_s21;
        double *error_p_s22;

        //int saveResult(DATA_BASE *dataBase);
};

//******************************************************************************

MEASURMENT_RESULTS::MEASURMENT_RESULTS(int cFreq)
{
  this->countOfFreq = cFreq;

  this->freq = new double[cFreq];

  this->m_s11 = new double[cFreq];
  this->m_s12 = new double[cFreq];
  this->m_s21 = new double[cFreq];
  this->m_s22 = new double[cFreq];

  this->error_m_s11 = new double[cFreq];
  this->error_m_s12 = new double[cFreq];
  this->error_m_s21 = new double[cFreq];
  this->error_m_s22 = new double[cFreq];

  this->p_s11 = new double[cFreq];
  this->p_s12 = new double[cFreq];
  this->p_s21 = new double[cFreq];
  this->p_s22 = new double[cFreq];

  this->error_p_s11 = new double[cFreq];
  this->error_p_s12 = new double[cFreq];
  this->error_p_s21 = new double[cFreq];
  this->error_p_s22 = new double[cFreq];
}

MEASURMENT_RESULTS::~MEASURMENT_RESULTS()
{
 delete[] freq;
 delete[] m_s11; delete[] error_m_s11; delete[] p_s11; delete[] error_p_s11;
 delete[] m_s12; delete[] error_m_s12; delete[] p_s12; delete[] error_p_s12;
 delete[] m_s21; delete[] error_m_s21; delete[] p_s21; delete[] error_p_s21;
 delete[] m_s22; delete[] error_m_s22; delete[] p_s22; delete[] error_p_s22;
}

//******************************************************************************
//******************************************************************************
//******************************************************************************
class FILE_READER
{
private:
        vector <string> fileStrings;
        int countOfStrings;
        String filePath;
public:
        FILE_READER(String path);
        int readDataFromFile();
        int takeResults(int numberOfResult, MEASURMENT_RESULTS *results);
};

FILE_READER::FILE_READER(String path)
{
  this->filePath = path;
}

int FILE_READER::readDataFromFile()
{
  ifstream file;
  file.open(filePath.c_str());
  if (file.is_open())
  {
     do
     {
        string str;
        getline(file, str);
        fileStrings.push_back(str);
        countOfStrings++;
       //freq   |s11|   pog|s11|        phase_s11       pog_phase_s11 4*4=16 16+1=17
     } while(file.eof()!=true);
     file.close();
     return 0;
  }
  else
  {
    return 1;
  }
}

int FILE_READER::takeResults(int numberOfResult, MEASURMENT_RESULTS *results)
{
bool isRead = false;
  if (this->fileStrings.size()!=0)
  {
     if (countOfStrings!=0)
     {
        int stringNumber = 0;
        int currentResult = 0;
        while (stringNumber < countOfStrings)
        {
           String str = fileStrings.at(stringNumber).c_str();
           if (str.Pos("---")!=0)
           {
                currentResult++;
                if (currentResult==numberOfResult)
                {
                  stringNumber++;
                  String str = fileStrings.at(stringNumber).c_str();
                  results->setElementType(str.Delete(1, str.Pos(":")+1));
                  stringNumber++;
                  str = fileStrings.at(stringNumber).c_str();
                  results->setSerNumberOfElement(str.Delete(1, str.Pos(":")+1));
                  stringNumber +=2;

                  int countOfLines = results->getCountOfFreq();
                  for (int j=0; j<countOfLines; j++)
                  { //freq
                    String tStr = fileStrings.at(stringNumber).c_str();
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->freq[j] = StrToFloat(tStr);
                    else results->freq[j] = 0;
                    
                    //|s11|
                    int sE = 1;
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->m_s11[j] = StrToFloat(tStr);
                    else results->m_s11[j] =0;
                    sE++;
                    //error |s11|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_m_s11[j] = StrToFloat(tStr);
                    else results->error_m_s11[j] = 0;
                    sE++;
                    //phase s11
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->p_s11[j] = StrToFloat(tStr);
                    else results->p_s11[j] = 0;
                    sE++;
                    //error phase s11
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_p_s11[j] = StrToFloat(tStr);
                    else results->error_p_s11[j] = 0;
                    sE++;

                    //|s12|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->m_s12[j] = StrToFloat(tStr);
                    else results->m_s12[j] = 0;
                    sE++;
                    //error |s12|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_m_s12[j] = StrToFloat(tStr);
                    else results->error_m_s12[j] = 0;
                    sE++;
                    //phase s12
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->p_s12[j] = StrToFloat(tStr);
                    else results->p_s12[j] = 0;
                    sE++;
                    //error phase s12
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_p_s12[j] = StrToFloat(tStr);
                    else results->error_p_s12[j] = 0;
                    sE++;

                    //|s21|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->m_s21[j] = StrToFloat(tStr);
                    else results->m_s21[j] = 0;
                    sE++;
                    //error |s21|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_m_s21[j] = StrToFloat(tStr);
                    else results->m_s21[j] = 0;
                    sE++;
                    //phase s21
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->p_s21[j] = StrToFloat(tStr);
                    else  results->p_s21[j] = 0;
                    sE++;
                    //error phase s21
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_p_s21[j] = StrToFloat(tStr);
                    else results->error_p_s21[j] = 0;
                    sE++;


                    //|s22|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->m_s22[j] = StrToFloat(tStr);
                    else results->m_s22[j] = 0;
                    sE++;
                    //error |s22|
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_m_s22[j] = StrToFloat(tStr);
                    else results->error_m_s22[j] = 0;
                    sE++;
                    //phase s22
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->p_s22[j] = StrToFloat(tStr);
                    else results->p_s22[j] = 0;
                    sE++;
                    //error phase s22
                    tStr = fileStrings.at(stringNumber).c_str();
                    for (int k=0;  k<sE; k++) tStr.Delete(1, tStr.Pos("\t"));
                    tStr.Delete(tStr.Pos("\t"), tStr.Length());
                    if (tStr!="-") results->error_p_s22[j] = StrToFloat(tStr);
                    else  results->error_p_s22[j] = 0;

                    if (j!=countOfLines-1) stringNumber++;
                  }
                  isRead = true;
                  break;
                }
           } //end if (str.Pos("---")!=0)
           stringNumber++;
        } //end while

        if (isRead)
        {
          return 0;  //��������� �������
        }
        else
        {
          return 3; //� ����� �� ���������� ������
        }
     }
     else
     {
       return 2; //� ���� ��� ������
     }
  }
  else
  {
    return 1; //��� ������ �����
  }
}
//******************************************************************************

int main(int argc, char* argv[])
{
MEASURMENT_RESULTS *results = new MEASURMENT_RESULTS(8);

String path = GetCurrentDir()+"\\Protocol.ini";
FILE_READER *reader = new FILE_READER(path);
reader->readDataFromFile();
String S1, S2;
double *freq = new double[8];
reader->takeResults(3, results);

for (int i=0; i<results->getCountOfFreq(); i++)
{
  cout << results->freq[i] << "\t";

  cout << results->m_s11[i] << "\t";
  cout << results->error_m_s11[i] << "\t";
  cout << results->p_s11[i] << "\t";
  cout << results->error_p_s11[i] << "\t";

  cout << results->m_s12[i] << "\t";
  cout << results->error_m_s12[i] << "\t";
  cout << results->p_s12[i] << "\t";
  cout << results->error_p_s12[i] << "\t";

  cout << results->m_s21[i] << "\t";
  cout << results->error_m_s21[i] << "\t";
  cout << results->p_s21[i] << "\t";
  cout << results->error_p_s21[i] << "\t";

  cout << results->m_s22[i] << "\t";
  cout << results->error_m_s22[i] << "\t";
  cout << results->p_s22[i] << "\t";
  cout << results->error_p_s22[i] << "\t";

  cout << endl;
}

system("pause");
return 0;
}
//---------------------------------------------------------------------------
 /*
 double *freq,
                            double *m_s11, double *error_m_s11, double *p_s11, double *error_p_s11,
                            double *m_s12, double *error_m_s12, double *p_s12, double *error_p_s12,
                            double *m_s21, double *error_m_s21, double *p_s21, double *error_p_s21,
                            double *m_s22, double *error_m_s22, double *p_s22, double *error_p_s22)
 */