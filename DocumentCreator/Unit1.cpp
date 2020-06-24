//---------------------------------------------------------------------------
#include <vcl.h>
#include <comobject>
#include <utilcls.h>
#include <windows.h>
#include <string>
#include <Classes.hpp>
#include <iostream>
#include <fstream>
#include <vector>
#pragma hdrstop
//---------------------------------------------------------------------------
#pragma argsused
using namespace std;
class DocumentCreator {
private:
        String filePath;

        Variant vVarApp;
        Variant vVarBooks;
        Variant vVarBook;
        Variant vVarSheets;
        Variant vVarSheet;
        Variant vVarCells;
        Variant vVarCell;
        Variant vVarDocs;
        Variant vVarDoc;
        Variant vVarTable;

        String fileName;
        String docType;
        String verificationType;
        String militaryBaseName;
        String number;
        String deviceName;
        String textEtalon;
        String deviceSerNumber;
        String deviceOwnerName;
        String workerName;
        String bossName;
        String reason;
        String dateOfCreation;
        String finishDate;
        bool print;
        
public:
  DocumentCreator(String Path){
      ifstream fileIn(Path.c_str());
      vector<String> *strs = new vector<String>();
      string out_s;
      while(getline(fileIn, out_s)) {
        String  str = out_s.c_str();
        strs->push_back(out_s.c_str());
      }
      fileIn.close();
      fileName = strs->at(0);
      docType = strs->at(1);
      verificationType = strs->at(2);
      militaryBaseName = strs->at(3);
      number = strs->at(4);
      deviceName = strs->at(5);
      textEtalon = strs->at(6);
      deviceSerNumber = strs->at(7);
      deviceOwnerName = strs->at(8);
      workerName = strs->at(9);
      bossName = strs->at(10);
      reason = strs->at(11);
      dateOfCreation = strs->at(12);
      finishDate = strs->at(13);
  }

  String getDocType(){
     return this->docType;
  }
  
  int  createNotification()
  {
     //������ �������� ��� ���������
     String from = GetCurrentDir() + "\\files\\protocolsTemplates\\PKM.docx";
     String to = GetCurrentDir() + "\\Documents\\" + this->fileName;
     CopyFile((LPCTSTR)from.c_str(), (LPCTSTR)to.c_str(), false);

     vVarApp = CreateOleObject("Word.Application");
     vVarApp.OlePropertySet("Visible",true);
     vVarDocs=vVarApp.OlePropertyGet("Documents");
     vVarDocs.OleProcedure("Open", to.c_str(),false,0);
     vVarDoc=vVarDocs.OleFunction("Item",1);
     vVarDoc.OleProcedure("Activate");
     vVarTable = vVarDoc.OlePropertyGet("Tables").OleFunction("Item", 1);

     Variant vVarCell, RangeCell;
     for(int i=0; i<7; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 4, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", militaryBaseName.c_str());
     }
     vVarCell = vVarTable.OleFunction("Cell", 24, 7);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", militaryBaseName.c_str());

     number = "� " + number;
     for(int i=0; i<4; i=i+3)
     {
       vVarCell = vVarTable.OleFunction("Cell", 10, 1+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", number.c_str());
     }

     //��� �������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 12, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", deviceName.c_str());
     }

     //������/�������� ���������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 12, i+1);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", textEtalon.c_str());
     }

     //�������� �����
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 14, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", deviceSerNumber.c_str());
     }

     //��������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 16, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", deviceOwnerName.c_str());
     }

     //����������
     vVarCell = vVarTable.OleFunction("Cell", 24, 2);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", workerName.c_str());
     vVarCell = vVarTable.OleFunction("Cell", 28, 7);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", workerName.c_str());

     //���������
     vVarCell = vVarTable.OleFunction("Cell", 26, 7);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", bossName.c_str());

     //������� ������������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 19, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", reason.c_str());
     }
     
     //���� ��������
     for(int i=0; i<6; i=i+4)
     {
        vVarCell = vVarTable.OleFunction("Cell", 31, i+1);
        RangeCell = vVarCell.OlePropertyGet("Range");
        RangeCell.OlePropertySet("Text", dateOfCreation.c_str());
     }
     
     //���������� � ��������
     vVarDoc.OleProcedure("Save");
     if (print)
        vVarDoc.OleProcedure("PrintOut");
     return 0;
  }

//------------------------------------------------------------------------------
  int createCertificate(){
     //������ ������������� ��� ��������
     String from = GetCurrentDir()+"\\files\\protocolsTemplates\\SVD.docx";
     String to = GetCurrentDir()+"\\Documents\\" + this->fileName;
     CopyFile((LPCTSTR)from.c_str(), (LPCTSTR)to.c_str(), false);

     vVarApp = CreateOleObject("Word.Application");
     vVarApp.OlePropertySet("Visible",true);
     vVarDocs=vVarApp.OlePropertyGet("Documents");
     vVarDocs.OleProcedure("Open", to.c_str(),false,0);
     vVarDoc=vVarDocs.OleFunction("Item",1);
     vVarDoc.OleProcedure("Activate");
     vVarTable = vVarDoc.OlePropertyGet("Tables").OleFunction("Item", 1);

     Variant vVarCell, RangeCell;
     //�������� �����
     for(int i=0; i<7; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 4, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", militaryBaseName.c_str());
     }
     vVarCell = vVarTable.OleFunction("Cell", 25, 9);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", militaryBaseName.c_str());

     //���� �������� �������������
     vVarCell = vVarTable.OleFunction("Cell", 12, 5);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->finishDate.c_str());

     //����� �������������
     for(int i=0; i<4; i=i+3)
     {
       vVarCell = vVarTable.OleFunction("Cell", 10, 1+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", this->number.c_str());
     }

     //������/�������� ���������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 14, i+1);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", this->textEtalon.c_str());
     }

     //��� �������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 14, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", this->deviceName.c_str());
     }

     //�������� �����
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 16, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", this->deviceSerNumber.c_str());
     }

     //��������
     for(int i=0; i<6; i=i+5)
     {
       vVarCell = vVarTable.OleFunction("Cell", 18, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", this->deviceOwnerName.c_str());
     }
     
     //���������/�������������
     for(int i=0; i<5; i=i+4)
     {
       vVarCell = vVarTable.OleFunction("Cell", 21, 2+i);
       RangeCell = vVarCell.OlePropertyGet("Range");
       RangeCell.OlePropertySet("Text", this->verificationType.c_str());
     }
     //����������
     vVarCell = vVarTable.OleFunction("Cell", 28, 2);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->workerName.c_str());
     //���������
     vVarCell = vVarTable.OleFunction("Cell", 29, 7);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->bossName.c_str());
     //���� ��������
     vVarCell = vVarTable.OleFunction("Cell", 31, 1);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->dateOfCreation.c_str());

     //���������� ������ ������� (��������� �������)
     vVarTable = vVarDoc.OlePropertyGet("Tables").OleFunction("Item", 2);

     //��� �������� ���������
     vVarCell = vVarTable.OleFunction("Cell", 2, 2);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->deviceName.c_str());

     //����������
     String text = "�� �� ����������� ������� ��������";
     vVarCell = vVarTable.OleFunction("Cell", 4, 2);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", text.c_str());
     text = "������";
     vVarCell = vVarTable.OleFunction("Cell", 5, 1);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", text.c_str());

     //����������
     vVarCell = vVarTable.OleFunction("Cell", 23, 2);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->workerName.c_str());

     //����
     vVarCell = vVarTable.OleFunction("Cell", 25, 1);
     RangeCell = vVarCell.OlePropertyGet("Range");
     RangeCell.OlePropertySet("Text", this->finishDate.c_str());
     
     //���������� � ��������
     vVarDoc.OleProcedure("Save");
     if (print)
        vVarDoc.OleProcedure("PrintOut");
     return 0;
  }                                     

};
//---------------------------------------------------------------------------

int main(int argc, char* argv[])
{
   CoInitialize(0);
   String Path = "proto.txt";
   DocumentCreator *creator = new DocumentCreator(Path);
   creator->createCertificate();
   return 0;
}