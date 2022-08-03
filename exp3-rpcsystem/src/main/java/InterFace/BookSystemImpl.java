// BookSystem
package InterFace;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// Remote����ʵ���࣬����̳�UnicastRemoteObject�࣬���븲д�ӿ��е�ȫ�����󷽷�
// �����ж����鼮ϵͳ��ص����ݽṹ�Ͳ��ݷ���
public class BookSystemImpl extends UnicastRemoteObject implements BookSystemInt {
	public BookSystemImpl() throws RemoteException {    // ���붨�幹�췽������RemoteException
		super();
	}
	private static final long serialVersionUID = 1L;    // ��������static final serialVersionUID field of type long  
	private ArrayList<Book> bookList = new ArrayList<Book>();    // ��ʼ���鼮�б�Ϊ�գ�����һ��Ҫ��new ArrayList<Book>()������ᱨ���ָ��
	
    public boolean add(Book book) throws RemoteException{    // ����һ���鼮���󣬷�����ӳɹ�/ʧ�ܱ�־
    	for(Book b :bookList){    // ����Ƿ���ͬID�鼮�������򷵻�false
            if(b.bookID.equals(book.bookID)){
                return false;
            }
        }
    	bookList.add(book);
    	return true;
    }

    public Book queryByID(String BookID) throws RemoteException{    // ��ѯָ��ID���鼮���󣬷��ظ��鼮����
        for(Book b :bookList){
            if(b.bookID.equals(BookID)){
                return b;
            }
        }
        return null;
    }
    
    public ArrayList<Book> queryByName(String name) throws RemoteException{    // ��������ѯ�����������鼮���󣬷����鼮�����б�
        ArrayList<Book> resultList = new ArrayList<Book>();
        for(Book b :bookList){
            if(b.bookName.indexOf(name, 0) != -1){
            	resultList.add(b);
            }
        }
        return resultList;
    }
    
    public boolean delete(String BookID) throws RemoteException{    // ɾ��ָ��ID���鼮���󣬷���ɾ���ɹ�/ʧ�ܱ�־
        for(Book b :bookList){
            if(b.bookID.equals(BookID)){
                bookList.remove(b);
                return true;
            }
        }
        return false;
    }
    
    public boolean alter(String BookID, String newName) throws RemoteException{    // �޸�ָ��ID���鼮�������ƣ������޸ĳɹ�/ʧ�ܱ�־
        for(Book b :bookList){
            if(b.bookID.equals(BookID)){
                b.bookName = newName;
                return true;
            }
        }
        return false;
    }
    
    public StringBuilder printBookList() throws RemoteException{    // ��ӡ�����鼮�����б������޶������ӡnone.
    	StringBuilder str = new StringBuilder();
    	StringBuilder none = new StringBuilder();
    	if(bookList.size() == 0) {
    		none.append("none.");
    		return none;
    	}
    	else {
            for(Book b :bookList){
            	str.append(b.BookInfo());
            	str.append('\n');
            }
            return str;
    	}       
    }
}