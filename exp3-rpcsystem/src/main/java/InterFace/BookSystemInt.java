// BookSystemInfo
package InterFace;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

// �ӿڶ����࣬��������Remote����
// �ӿ������̳�Remote��
// ���з�����Ҫ����java.rmi.RemoteException�쳣
public interface BookSystemInt extends Remote{
    boolean add(Book b) throws RemoteException;    // ����һ���鼮���󣬷�����ӳɹ�/ʧ�ܱ�־
    Book queryByID(String bookID) throws RemoteException;    // ��ѯָ��ID���鼮���󣬷��ظ��鼮����
    ArrayList<Book> queryByName(String name) throws RemoteException;    // ��������ѯ�����������鼮���󣬷����鼮�����б�
    boolean delete(String BookID) throws RemoteException;    // ɾ��ָ��ID���鼮���󣬷���ɾ���ɹ�/ʧ�ܱ�־
    boolean alter(String BookID, String newName) throws RemoteException;    // ɾ��ָ��ID���鼮���󣬷���ɾ���ɹ�/ʧ�ܱ�־
    StringBuilder printBookList() throws RemoteException;    // ��ӡ�����鼮�����б��ڷ�������ִ��
}