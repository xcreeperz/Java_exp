import java.io.*;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author Sagume �ŷ��
 */
public class Inventory {
    public static void main(String[] args) throws IOException {

        //������ά�����Ͳ����б�
        List<Trans> lTrans = new ArrayList();
        List<Goods> lGoods = new ArrayList();
        List<Ship> lShip = new ArrayList();

        //�����ж�ȡ���
        FileReader fr = new FileReader("Inventory.txt");
        BufferedReader br = new BufferedReader(fr);

        String line1;
        String[] temp = new String[4];

        //ʹ����Ƭ������ȡ��Ϣ����������������
        while((line1 = br.readLine()) != null){
            temp = line1.split("\t");
            lGoods.add(new Goods(temp[0], Integer.parseInt(temp[1]), temp[2], temp[3]));
        }

        br.close();
        fr.close();


        //�����ж�ȡ����
        FileReader fr2 = new FileReader("Transactions.txt");
        BufferedReader br2 = new BufferedReader(fr2);

        String line2;
        String[] temp1 = new String[5];

        //ʹ��ͬ����ʽ��ʼ����������������飬���������ǩ
        while((line2 = br2.readLine()) != null){
            temp1 = line2.split("\t");
            char ck = temp1[0].charAt(0);
            int Q;

            switch(ck){
                case 'O': Q = Integer.parseInt(temp1[2]); lTrans.add(new Trans(ck, temp1[1], Q, temp[3], "1"));  break;
                case 'A': lTrans.add(new Trans(ck, temp1[1], 0, temp1[2], temp1[3])); break;
                case 'R': Q = Integer.parseInt(temp1[2]); lTrans.add(new Trans(ck, temp1[1], Q, "a", "1")); break;
                case 'D': lTrans.add(new Trans(ck, temp1[1], 0, "a", "1")); break;
                default:;
            }

        }

        br2.close();
        fr2.close();


        //��������ǩ���ȼ�����
        lTrans.sort(new Comparator<Trans>() {
            @Override
            public int compare(Trans t1, Trans t2) {
                return t1.prior - t2.prior;     //�����а����ȼ�����
            }
        });

        int startO = 0, endO = 0;
        for(int i = 0; i < lTrans.size(); i++){
            if(lTrans.get(i).op == 'O' && (i == 0 || lTrans.get(i - 1).op != 'O')){
                startO = i;
            }
            if(lTrans.get(i).op == 'O' && (i == lTrans.size() - 1 || lTrans.get(i + 1).op != 'O')){
                endO = i + 1;
            }
        }

        //�Գ����������������У���֤�л��ɳ�
        lTrans.subList(startO, endO).sort(new Comparator<Trans>() {
            @Override
            public int compare(Trans t1, Trans t2) {
                    if (Objects.equals(t1.tNum, t2.tNum)) {
                        return t1.tQt - t2.tQt;
                    }else{
                        return 0;
                    }
                }
        });


        FileWriter ship = new FileWriter("Shipping.txt");
        BufferedWriter shp = new BufferedWriter(ship);

        FileWriter error = new FileWriter("Errors.txt");
        BufferedWriter ero = new BufferedWriter(error);

        for(Trans trans : lTrans){
            //���� A
            if(trans.prior == 1){
                lGoods.add(new Goods(trans.tNum, trans.tQt, trans.Cus, trans.tDesc));
                System.out.println("ִ��A");
            }

            //����R
            else if(trans.prior == 2){
                for(Goods lg : lGoods){
                    if(Objects.equals(lg.iNum, trans.tNum)){
                        lg.Qt += trans.tQt;
                        break;
                    }

                }
                System.out.println("ִ��R");
            }

            //����O
            else if(trans.prior == 3){
                for(Goods rg : lGoods){
                    //���Ҷ�Ӧ�����
                    if(Objects.equals(rg.iNum, trans.tNum)){
                        //��ȡ��Ӧ�̱��
                        trans.Cus = rg.Sup;
                        //����л�
                        if(rg.Qt - trans.tQt > 0){
                            rg.Qt -= trans.tQt;
                            //���³�����¼������ջ򲻴��ھ�ֱ���½���������ھ͸�������
                            if(lShip.isEmpty()){
                                lShip.add(new Ship(trans.Cus, trans.tNum, trans.tQt));
                            }else{
                                boolean exists = false;
                                for(Ship sp : lShip){
                                    if(Objects.equals(trans.Cus, sp.Cus) && Objects.equals(trans.tNum, sp.Num)) {
                                        exists = true;
                                        sp.Num += trans.tNum;
                                    }
                                }
                                if(!exists){
                                    lShip.add(new Ship(trans.Cus, trans.tNum, trans.tQt));
                                }
                            }
                        }
                        //����޻�
                        else{
                            ero.write(trans.Cus);
                            ero.write('\t');
                            ero.write(trans.tNum);
                            ero.write('\t');
                            ero.write(Integer.toString(trans.tQt));
                            ero.newLine();
                            ero.flush();
                        }
                    }
                }
                System.out.println("ִ��O");
            }

            else{
                int index = 0;
                boolean find = false;
                for(Goods dg : lGoods){
                    if(Objects.equals(dg.iNum, trans.tNum)){
                        if(dg.Qt != 0){
                            ero.write('0');
                            ero.write('\t');
                            ero.write(dg.iNum);
                            ero.write('\t');
                            ero.write(Integer.toString(dg.Qt));
                            ero.newLine();
                            ero.flush();
                        }else{
                            find = true;
                            index = lGoods.indexOf(dg);
                        }
                    }
                }
                if(find) {
                    lGoods.remove(index);
                }

                System.out.println("ִ��D");
            }
        }

        for(Ship lsp : lShip) {
            shp.write(lsp.toString());
            shp.newLine();
            shp.flush();
        }

        //�������Ŵ�С�������򣬽������������ǰ��
        lGoods.sort(new Comparator<Goods>() {
            @Override
            public int compare(Goods g1, Goods g2) {
                return Integer.parseInt(g1.iNum) - Integer.parseInt(g2.iNum);
            }
        });

        FileWriter NIn = new FileWriter("NewInventory.txt");
        BufferedWriter Nbw = new BufferedWriter(NIn);

        for(Goods lgd : lGoods){
            Nbw.write(lgd.toString());
            Nbw.newLine();
            Nbw.flush();
        }

    }
}


class Goods{
    String iNum;
    int Qt;
    String Sup;
    String Desc;

    //��ʼ��
    public Goods(String id, int num, String sp, String des)
    {
        iNum = id;
        Qt = num;
        Sup = sp;
        Desc = des;
    }

    @Override
    public String toString(){
        return this.iNum + '\t' + Integer.toString(this.Qt) + '\t' + this.Sup + '\t' + this.Desc;
    }

}

class Trans{
    char op;
    String tNum;
    int tQt;
    String Cus;
    String tDesc;
    int prior;

    //��ʼ��
    public Trans(char OP, String tN, int tQ, String tS, String tD){
        op = OP;
        tNum = tN;
        tQt = tQ;
        Cus = tS;
        tDesc = tD;
        switch (op){
            case 'A':   prior = 1;  break;
            case 'R':   prior = 2;  break;
            case 'O':   prior = 3;  break;
            case 'D':   prior = 4;  break;
            default:;
        }
    }
}

class Ship{
    String Cus;
    String Num;
    int qt;

    public Ship(String cus, String num, int Qt){
        Cus = cus;
        Num = num;
        qt = Qt;
    }

    @Override
    public String toString(){
        return this.Cus + '\t' + this.Num + '\t' + Integer.toString(this.qt);
    }
}