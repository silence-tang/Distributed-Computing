//  Book
package InterFace;

import java.io.Serializable;

// �鼮�ࣺ�������������Լ�����,���붨��Ϊ������
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;
	// �鼮������
    public String bookID;
    public String bookName;
    // ���췽��
    public Book(String id, String name){
    	bookID = id;
    	bookName = name;
    }
    // BookInfo�����������鼮��Ϣ
    public String BookInfo(){
        return("�鼮ID: " + bookID + " ����: " + bookName);
    }
}