#include <iostream>
#include <ctime>
#include <cstdio>
#include <fstream> 
#include <cstdlib>
#include <cstring>
#include <algorithm>

#include <string>
#include "tree.cpp"

using namespace std;

int ContentSize = 10;

int main(){
    //ifstream fin("data_input.txt");
	//ofstream fout("data_output.txt");
	//int ty, id, key;
	//fin >> n >> ContentSize;
    string str1 = "0010011011";
    string str2 = "1000011011";
    string str3 = "0110000101";
    //string str4 = "0010011011";
    //string str5 = "1000011011";
    //string str6 = "0110000101";
    //string str7 = "0010011011";
    //string str8 = "1000011011";
    //string str9 = "0110000101";
    //string str10 = "0010011011";
    
	ScapegoatTree Tree(ContentSize);
	bool *content = new bool[ContentSize];	
	for(int j = 0; j < ContentSize; ++j)
		content[j] = int(str1[j]) - 48;
	Tree.Insert(1, content);
	for(int j = 0; j < ContentSize; ++j)
		content[j] = int(str2[j]) - 48;
    Tree.Insert(2, content);
	for(int j = 0; j < ContentSize; ++j)
		content[j] = int(str3[j]) - 48;
	Tree.Insert(3, content);

    cout << "hello!" << endl;
    int size = 0;
    int *res = NULL;
    Tree.Query(9, res, size);
    if(size > 0)
        sort(res, res + size);

	    for(int j = 0; j <= size - 1; ++j)
		    cout << res[j] << " ";
	    delete [] res;
//cout << size << endl;
    cout << endl;
    string s = Tree.Graph();
    cout << s << endl;
    return 0;
}
