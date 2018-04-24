DSSE-GraphDB Server
=====================
### *Dynamic Searchable Symmetric Encryption Based on Graph Database*
 
This fold provides our implementation of dynamic SSE algorthim and interprets how to employ it on the graph database (Neo4j). We use keyword balanced binary tree to keep efficency in keyword searching. For more information, you can check our report [here](https://github.com/wangjksjtu/SJTU-SSE/blob/master/docs/%E5%9F%BA%E4%BA%8E%E5%9B%BE%E6%95%B0%E6%8D%AE%E5%BA%93%E7%9A%84%E5%8F%AF%E6%90%9C%E7%B4%A2%E5%8A%A0%E5%AF%86%E7%B3%BB%E7%BB%9F.pdf) where we give **detailed algorthims**, **implementations** and **tests**.

### Environment ###

* Python2.7 
* Django1.10 
* Django rest-framework 3
###

    cd unauth_server/
    g++ -o treeLib.so -shared -fPIC ciphertext/tree.cpp
    python manage.py runserver [0.0.0.0:8000]

You can visit *http://localhost:8000/* to check the API.
