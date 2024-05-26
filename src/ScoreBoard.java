

import Table.Cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author ZhouLirong
 */
public class ScoreBoard {
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JButton next = new JButton("Next");
    private boolean clicked = false;
    private static final Object lock = new Object();
    private JFrame jf=new JFrame();
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请分别输入各部件的延迟，加法部件延迟：");
        int addTime=scanner.nextInt();
        System.out.print("乘法部件延迟：");
        int multTime=scanner.nextInt();
        System.out.print("除法部件延迟：");
        int divTime=scanner.nextInt();
        // 获取输入的字符串数量
        System.out.print("请输入指令的数量：");
        int count = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符
        // 创建一个字符串数组
        Instruction[] instructions = new Instruction[count];
        System.out.println("请输入指令：");
        // 逐个获取输入的字符串
        for (int i = 0; i < count; i++) {
            String input = scanner.nextLine();
            instructions[i]=new Instruction(input);
            instructions[i].setInst(input);
        }
        // 关闭输入流
        scanner.close();
        new ScoreBoard().algorithm(instructions,addTime,multTime,divTime);
    }
    public void algorithm(Instruction[] instructions,int addTime,int multTime,int divTime){
        jf=TableInst(instructions);
        FU[] fus=new FU[5];
        fus[0]=new FU("Integer",0);
        fus[1]=new FU("Add",addTime);
        fus[2]=new FU("Mult1",multTime);
        fus[3]=new FU("Mult2",multTime);
        fus[4]=new FU("Div",divTime);
        Map<String, String> result = new HashMap<>(20);
        for (int i = 0; i <= 30; i += 2) {
            result.put("F" + i, null);
        }
        for(int cycle=1;;cycle++) {
            clicked=false;
            int i;
            for (i = 0; i < instructions.length; i++) {
                if (!instructions[i].getFinish()) {
                    break;
                }
            }
            if (i >= instructions.length) {
                break;
            }
            for (i = 0; i < instructions.length; i++) {
                if ("LD".equals(instructions[i].getOp())) {
                    if (instructions[i].getIssue() == 0) {
                        if (!fus[0].getbusy() && result.get(instructions[i].getFi()) == null) {
                            issue(fus[0], instructions[i], result);
                            instructions[i].setIssue(cycle);
                            break;
                        } else {
                            break;
                        }
                    }
                } else if ("ADDD".equals(instructions[i].getOp()) || "SUBD".equals(instructions[i].getOp())) {
                    if (instructions[i].getIssue() == 0) {
                        if (!fus[1].getbusy() && result.get(instructions[i].getFi()) == null) {
                            issue(fus[1], instructions[i], result);
                            instructions[i].setIssue(cycle);
                            break;
                        } else {
                            break;
                        }
                    }
                } else if ("MULTD".equals(instructions[i].getOp())) {
                    if (instructions[i].getIssue() == 0) {
                        if (!fus[2].getbusy() && result.get(instructions[i].getFi()) == null) {
                            instructions[i].setFu(fus[2]);
                            issue(fus[2], instructions[i], result);
                            instructions[i].setIssue(cycle);
                            break;
                        } else if (!fus[3].getbusy() && result.get(instructions[i].getFi()) == null) {
                            instructions[i].setFu(fus[3]);
                            issue(fus[3], instructions[i], result);
                            instructions[i].setIssue(cycle);
                            break;
                        }
                    }
                } else if ("DIVD".equals(instructions[i].getOp())) {
                    if (instructions[i].getIssue() == 0) {
                        if (!fus[4].getbusy() && result.get(instructions[i].getFi()) == null) {
                            issue(fus[4], instructions[i], result);
                            instructions[i].setIssue(cycle);
                            break;
                        }
                    }
                }
            }
            for (i = 0; i < instructions.length; i++){
                if ("LD".equals(instructions[i].getOp())){
                    if (instructions[i].getIssue()!=cycle&&instructions[i].getIssue()!=0&&instructions[i].getRead() == 0) {
                        if (fus[0].getRj() && fus[0].getRk()) {
                            readOprand(fus[0]);
                            instructions[i].setRead(cycle);
                        }
                    }
                }else if("ADDD".equals(instructions[i].getOp())||"SUBD".equals(instructions[i].getOp())){
                    if (instructions[i].getIssue()!=cycle&&instructions[i].getIssue()!=0&&instructions[i].getRead() == 0){
                        if (fus[1].getRj() && fus[1].getRk()) {
                            readOprand(fus[1]);
                            instructions[i].setRead(cycle);
                            instructions[i].setExeCycle(cycle + fus[1].getTime());
                        }
                    }
                } else if ("MULTD".equals(instructions[i].getOp())) {
                    if (instructions[i].getIssue()!=cycle&&instructions[i].getIssue()!=0&&instructions[i].getRead() == 0){
                        if (instructions[i].getFu().getRj() && instructions[i].getFu().getRk()) {
                            readOprand(instructions[i].getFu());
                            instructions[i].setRead(cycle);
                            instructions[i].setExeCycle(cycle + instructions[i].getFu().getTime());
                        }
                    }
                } else if ("DIVD".equals(instructions[i].getOp())) {
                    if (instructions[i].getIssue()!=cycle&&instructions[i].getIssue()!=0&&instructions[i].getRead() == 0) {
                        if (fus[4].getRj() && fus[4].getRk()) {
                            readOprand(fus[4]);
                            instructions[i].setRead(cycle);
                            instructions[i].setExeCycle(cycle + fus[4].getTime());
                        }
                    }
                }
            }
            for (i = 0; i < instructions.length; i++){
                if ("LD".equals(instructions[i].getOp())) {
                    if (instructions[i].getRead()!=cycle&&instructions[i].getRead() != 0 && instructions[i].getExe() == 0) {
                        execute(fus[0]);
                        instructions[i].setExe(cycle);
                    }
                } else if ("ADDD".equals(instructions[i].getOp())||"SUBD".equals(instructions[i].getOp())) {
                    if (instructions[i].getRead()!=cycle&&instructions[i].getRead() != 0 && instructions[i].getExe() == 0) {
                        execute(fus[1]);
                        if (cycle == instructions[i].getExeCycle()) {
                            instructions[i].setExe(cycle);
                        }
                    }
                }else if("MULTD".equals(instructions[i].getOp())){
                    if (instructions[i].getRead()!=cycle&&instructions[i].getRead() != 0 && instructions[i].getExe() == 0) {
                        execute(instructions[i].getFu());
                        if (cycle == instructions[i].getExeCycle()) {
                            instructions[i].setExe(cycle);
                        }
                    }
                } else if ("DIVD".equals(instructions[i].getOp())) {
                    if (instructions[i].getRead()!=cycle&&instructions[i].getRead() != 0 && instructions[i].getExe() == 0) {
                        execute(fus[4]);
                        if (cycle == instructions[i].getExeCycle()) {
                            instructions[i].setExe(cycle);
                        }
                    }
                }
            }
            for(i = 0; i < instructions.length; i++){
                if ("LD".equals(instructions[i].getOp())) {
                    if(instructions[i].getExe()!=cycle&&instructions[i].getExe()!=0&&instructions[i].getWriteback() == 0) {
                        boolean flag = true;
                        for (FU fu : fus) {
                            if (fus[0].getFi().equals(fu.getFj()) && fu.getRj()) {
                                flag = false;
                            }
                            if (fus[0].getFi().equals(fu.getFk()) && fu.getRk()) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            writeback(fus, fus[0], result);
                            instructions[i].setWriteback(cycle);
                            instructions[i].setFinish(true);
                        }
                    }
                }
                else if("ADDD".equals(instructions[i].getOp())||"SUBD".equals(instructions[i].getOp())){
                    if(instructions[i].getExe()!=cycle&&instructions[i].getExe()!=0&&instructions[i].getWriteback() == 0) {
                        boolean flag = true;
                        for (FU fu : fus) {
                            if (fus[1].getFi().equals(fu.getFj()) && fu.getRj()) {
                                flag = false;
                            }
                            if (fus[1].getFi().equals(fu.getFk()) && fu.getRk()) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            writeback(fus, fus[1], result);
                            instructions[i].setWriteback(cycle);
                            instructions[i].setFinish(true);
                        }
                    }
                } else if ("MULTD".equals(instructions[i].getOp())) {
                    if (instructions[i].getExe()!=cycle&&instructions[i].getExe() != 0 && instructions[i].getWriteback() == 0) {
                        boolean flag = true;
                        for (FU fu1 : fus) {
                            if (instructions[i].getFu().getFi().equals(fu1.getFj()) && fu1.getRj()) {
                                flag = false;
                            }
                            if (instructions[i].getFu().getFi().equals(fu1.getFk()) && fu1.getRk()) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            writeback(fus, instructions[i].getFu(), result);
                            instructions[i].setWriteback(cycle);
                            instructions[i].setFinish(true);
                        }
                    }
                }else if("DIVD".equals(instructions[i].getOp())){
                    if (instructions[i].getExe()!=cycle&&instructions[i].getExe() != 0 && instructions[i].getWriteback() == 0) {
                        boolean flag = true;
                        for (FU fu : fus) {
                            if (fus[4].getFi().equals(fu.getFj()) && fu.getRj()) {
                                flag = false;
                            }
                            if (fus[4].getFi().equals(fu.getFk()) && fu.getRk()) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            writeback(fus, fus[4], result);
                            instructions[i].setWriteback(cycle);
                            instructions[i].setFinish(true);
                        }
                    }
                }
            }
            draw(cycle,instructions,fus,result,jf);
        }
    }
    public void issue(FU fu,Instruction instruction,Map<String, String> result){
        fu.setBusy(true);
        fu.setOp(instruction.getOp());
        fu.setFi(instruction.getFi());
        fu.setFk(instruction.getFk());
        fu.setQk(result.get(instruction.getFk()));
        if (result.get(instruction.getFk())==null){
            fu.setRk(true);
        }
        result.replace(instruction.getFi(),fu.getName());
        if(!"LD".equals(fu.getOp())) {
            fu.setFj(instruction.getFj());
            fu.setQj(result.get(instruction.getFj()));
            if (result.get(instruction.getFj())==null){
                fu.setRj(true);
            }
        }
        else{
            fu.setRj(true);
        }
    }
    public void readOprand(FU fu){
        fu.setQj(null);
        fu.setQk(null);
    }
    public void execute(FU fu){
        fu.setRj(false);
        fu.setRk(false);
    }
    public void writeback(FU[] fus,FU fu,Map<String, String> result){
        for(FU f:fus) {
            if (fu.getName().equals(f.getQj())) {
                f.setRj(true);
            }
            if (fu.getName().equals(f.getQk())) {
                f.setRk(true);
            }
        }
        result.replace(fu.getFi(),null);
        fu.setBusy(false);
        fu.setOp(null);
        fu.setFi(null);
        fu.setFj(null);
        fu.setFk(null);
        fu.setQj(null);
        fu.setQk(null);
        fu.setRj(false);
        fu.setRk(false);
    }
    public void draw(int cycle,Instruction[] instructions,FU[] fus,Map<String, String> result,JFrame jf) {
//        System.out.println("Cycle" + cycle);
//        System.out.println("指令状态表");
//        List<Cell> header1 = new ArrayList<Cell>(){{
//            add(new Cell("Instruction"));
//            add(new Cell("Issue"));
//            add(new Cell("ReadOprand"));
//            add(new Cell("Execution"));
//            add(new Cell("WriteBack"));
//        }};
//        List<List<Cell>> body1 = new ArrayList<>();
//        for (Instruction instruction : instructions) {
//            List<Cell> element = new ArrayList<>();
//            element.add(new Cell(instruction.getInst()));
//            element.add(new Cell(String.valueOf(instruction.getIssue())));
//            element.add(new Cell(String.valueOf(instruction.getRead())));
//            element.add(new Cell(String.valueOf(instruction.getExe())));
//            element.add(new Cell(String.valueOf(instruction.getWriteback())));
//            body1.add(element);
//        }
//        new Table.ConsoleTable.ConsoleTableBuilder().addHeaders(header1).addRows(body1).build().print();
//        System.out.println("功能部件状态表");
//        List<Cell> header2 = new ArrayList<Cell>(){{
//            add(new Cell("Name"));
//            add(new Cell("Busy"));
//            add(new Cell("Op"));
//            add(new Cell("Fi"));
//            add(new Cell("Fj"));
//            add(new Cell("Fk"));
//            add(new Cell("Qj"));
//            add(new Cell("Qk"));
//            add(new Cell("Rj"));
//            add(new Cell("Rk"));
//        }};
//        List<List<Cell>> body2 = new ArrayList<>();
//        for(int i=0;i<5;i++){
//            List<Cell> element=new ArrayList<>();
//            element.add(new Cell(fus[i].getName()));
//            element.add(new Cell(String.valueOf(fus[i].getbusy())));
//            element.add(new Cell(fus[i].getOp()));
//            element.add(new Cell(fus[i].getFi()));
//            element.add(new Cell(fus[i].getFj()));
//            element.add(new Cell(fus[i].getFk()));
//            element.add(new Cell(fus[i].getQj()));
//            element.add(new Cell(fus[i].getQk()));
//            element.add(new Cell(String.valueOf(fus[i].getRj())));
//            element.add(new Cell(String.valueOf(fus[i].getRk())));
//            body2.add(element);
//        }
//        new Table.ConsoleTable.ConsoleTableBuilder().addHeaders(header2).addRows(body2).build().print();
//        System.out.println("结果寄存器状态表");
//        List<Cell> header3 = new ArrayList<>();
//        for (int i = 0; i <= 30; i += 2) {
//            header3.add(new Cell("F" + i));
//        }
//        List<List<Cell>> body3 = new ArrayList<>();
//        List<Cell> element=new ArrayList<>();
//        for (int i = 0; i <= 30; i += 2) {
//            element.add(new Cell(result.get("F"+i)));
//        }
//        body3.add(element);
//        new Table.ConsoleTable.ConsoleTableBuilder().addHeaders(header3).addRows(body3).build().print();
        jf.setTitle("Cycle" + cycle);
        for (int i = 0; i < instructions.length; i++) {
            table1.setValueAt(String.valueOf(instructions[i].getIssue()), i, 1);
            table1.setValueAt(String.valueOf(instructions[i].getRead()), i, 2);
            table1.setValueAt(String.valueOf(instructions[i].getExe()), i, 3);
            table1.setValueAt(String.valueOf(instructions[i].getWriteback()), i, 4);
        }
        for (int i = 0; i < 5; i++) {
            table2.setValueAt(String.valueOf(fus[i].getbusy()), i, 1);
            table2.setValueAt(fus[i].getOp(), i, 2);
            table2.setValueAt(fus[i].getFi(), i, 3);
            table2.setValueAt(fus[i].getFj(), i, 4);
            table2.setValueAt(fus[i].getFk(), i, 5);
            table2.setValueAt(fus[i].getQj(), i, 6);
            table2.setValueAt(fus[i].getQk(), i, 7);
            table2.setValueAt(String.valueOf(fus[i].getRj()), i, 8);
            table2.setValueAt(String.valueOf(fus[i].getRk()), i, 9);
        }
        for (int i = 0; i < 1; i++) {
            for (int j = 1; j < 17; j++) {
                table3.setValueAt(result.get("F" + (2 * j - 2)), 0, j);
            }
        }
        jf.setVisible(true);
        synchronized (lock) {
            while (!clicked) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
        public JFrame TableInst (Instruction[]instructions)
        {
            jf.setSize(800, 800);
            jf.setLocationRelativeTo(null);
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setLayout(new GridLayout(4, 1,20,20));
            String[][] datas1 = new String[instructions.length][5];
            for (int i = 0; i < instructions.length; i++) {
                datas1[i][0] = instructions[i].getInst();
                for (int j = 1; j < 5; j++) {
                    datas1[i][j] = " "; // 使用空格填充
                }
            }
            String[] titles1 = {"Instruction", "Issue", "Oprand", "Execution", "Write"};
            table1 = new JTable(datas1, titles1);
            String[][] datas2 = new String[5][10];
            datas2[0][0] = "Integer";
            datas2[1][0] = "Add";
            datas2[2][0] = "Mult1";
            datas2[3][0] = "Mult2";
            datas2[4][0] = "Divide";
            for (int i = 0; i < 5; i++) {
                for (int j = 1; j < 10; j++) {
                    datas2[i][j] = " ";
                }
            }
            String[] titles2 = {"FUname", "Busy", "Op", "Fi", "Fj", "Fk", "Qj", "Qk", "Rj", "Rk"};
            table2 = new JTable(datas2, titles2);
            String[][] datas3 = new String[1][17];
            String[] titles3 = {"", "F0", "F2", "F4", "F6", "F8", "F10", "F12", "F14", "F16", "F18", "F20", "F22", "F24", "F26", "F28", "F30"};
            for (int i = 0; i < 1; i++) {
                datas3[i][0] = "Name";
                for (int j = 1; j < 17; j++) {
                    datas3[i][j] = " ";
                }
            }
            table3 = new JTable(datas3, titles3);
            next.setSize(80, 25);
            next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    synchronized (lock) {
                        clicked = true;
                        lock.notify();
                    }
                }
            });
            jf.add(new JScrollPane(table1));
            jf.add(new JScrollPane(table2));
            jf.add(new JScrollPane(table3));
            jf.add(next);
            return jf;
        }
    }
