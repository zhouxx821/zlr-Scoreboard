

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
    private Box box1;
    private Box box2;
    private Box box3;
    private JButton next = new JButton("Next");
    private boolean clicked = false;
    private static final Object lock = new Object();
    private static final Object lock2 = new Object();
    private JFrame jf=new JFrame();
    private int addTime;
    private int multTime;
    private int divTime;
    private int number;
    public static void main(String[] args)
    {
        ScoreBoard scoreBoard=new ScoreBoard();
        if("ConsolePrint".equals(args[0])){
            Instruction[] instructions= scoreBoard.init2();
            scoreBoard.algorithm(instructions,args[0]);
        } else if ("GUIPrint".equals(args[0])) {
            Instruction[] instructions=scoreBoard.init1();
            scoreBoard.algorithm(instructions,args[0]);
        }
    }
    //记分牌算法具体实现
    public void algorithm(Instruction[] instructions,String outway){
        //新建五个FunctionalUnit功能部件对象
        FU[] fus=new FU[5];
        fus[0]=new FU("Integer",0);
        fus[1]=new FU("Add",addTime);
        fus[2]=new FU("Mult1",multTime);
        fus[3]=new FU("Mult2",multTime);
        fus[4]=new FU("Div",divTime);
        //寄存器Result
        Map<String, String> result = new HashMap<>(20);
        for (int i = 0; i <= 30; i += 2) {
            result.put("F" + i, null);
        }
        for(int cycle=1;;cycle++) {
            clicked=false;
            int i;
            //如果所有的指令均执行完毕，则退出循环
            for (i = 0; i < instructions.length; i++) {
                if (!instructions[i].getFinish()) {
                    break;
                }
            }
            if (i >= instructions.length) {
                break;
            }
            //判断每一条指令是否能够流出，若某条指令流出，则退出循环
            for (i = 0; i < instructions.length; i++) {
                if ("LD".equals(instructions[i].getOp())) {
                    //判断指令是否已经流出
                    if (instructions[i].getIssue() == 0) {
                        //判断指令所需功能部件是否Busy以及是否存在WAW冲突
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
                        //对于乘法指令，有mult1和mult2两个功能部件，故先判断mult1，若不满足条件则判断mult2
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
            //判断每一条指令是否能够读操作数
            for (i = 0; i < instructions.length; i++){
                if ("LD".equals(instructions[i].getOp())){
                    //判断指令是否已经流出并且还未读操作数
                    if (instructions[i].getIssue()!=cycle&&instructions[i].getIssue()!=0&&instructions[i].getRead() == 0) {
                        //判断源操作数的可用性，避免RAW冲突
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
                            //对于需要不同执行周期的指令，保存需要执行的周期
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
                        //在执行前期改写其Rj和Rk的值
                        execute(fus[0]);
                        instructions[i].setExe(cycle);
                    }
                } else if ("ADDD".equals(instructions[i].getOp())||"SUBD".equals(instructions[i].getOp())) {
                    if (instructions[i].getRead()!=cycle&&instructions[i].getRead() != 0 && instructions[i].getExe() == 0) {
                        execute(fus[1]);
                        //判断其是否已经执行完毕
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
            //判断指令是否能够写结果
            for(i = 0; i < instructions.length; i++){
                if ("LD".equals(instructions[i].getOp())) {
                    if(instructions[i].getExe()!=cycle&&instructions[i].getExe()!=0&&instructions[i].getWriteback() == 0) {
                        boolean flag = true;
                        //判断是否有正在执行的指令需要读当前指令的目的寄存器的值
                        for (FU fu : fus) {
                            if (fus[0].getFi().equals(fu.getFj()) && fu.getRj()) {
                                flag = false;
                            }
                            if (fus[0].getFi().equals(fu.getFk()) && fu.getRk()) {
                                flag = false;
                            }
                        }
                        //若没有，则不存在WAR冲突，进行写结果操作，并记录该指令已完成执行
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
            //每一个周期结束画出记分牌的三个表
            if("GUIPrint".equals(outway)) {
                draw1(cycle, instructions, fus, result);
            }
            else {
                draw2(cycle,instructions,fus,result);
            }
        }
    }
    //流出阶段执行的操作，修改表中指令状态、功能部件状态以及寄存器result的值
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
        //对LD指令特殊判断，此代码中将源寄存器设置为Fk，Fj不存在，其Rj仍满足条件
        else{
            fu.setRj(true);
        }
    }
    //读操作数阶段
    public void readOprand(FU fu){
        fu.setQj(null);
        fu.setQk(null);
    }
    //执行阶段修改Rj、Rk值
    public void execute(FU fu){
        fu.setRj(false);
        fu.setRk(false);
    }
    //写结果阶段
    public void writeback(FU[] fus,FU fu,Map<String, String> result){
        //将其他指令的源寄存器为该指令的目的寄存器的Rj、Rk值改为就绪
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
    public void draw1(int cycle,Instruction[] instructions,FU[] fus,Map<String, String> result) {
        //GUI界面输出代码，在每一个周期对表格每一个空填空
        jf.setTitle("Cycle"+cycle);
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
        //只有当按下按钮时才执行下一周期，否则等待
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
    public void draw2(int cycle,Instruction[] instructions,FU[] fus,Map<String, String> result) {
        //控制台输出代码
        System.out.println("Cycle" + cycle);
        System.out.println("指令状态表");
        List<Cell> header1 = new ArrayList<Cell>(){{
            add(new Cell("Instruction"));
            add(new Cell("Issue"));
            add(new Cell("ReadOprand"));
            add(new Cell("Execution"));
            add(new Cell("WriteBack"));
        }};
        List<List<Cell>> body1 = new ArrayList<>();
        for (Instruction instruction : instructions) {
            List<Cell> element = new ArrayList<>();
            element.add(new Cell(instruction.getInst()));
            element.add(new Cell(String.valueOf(instruction.getIssue())));
            element.add(new Cell(String.valueOf(instruction.getRead())));
            element.add(new Cell(String.valueOf(instruction.getExe())));
            element.add(new Cell(String.valueOf(instruction.getWriteback())));
            body1.add(element);
        }
        new Table.ConsoleTable.ConsoleTableBuilder().addHeaders(header1).addRows(body1).build().print();
        System.out.println("功能部件状态表");
        List<Cell> header2 = new ArrayList<Cell>(){{
            add(new Cell("Name"));
            add(new Cell("Busy"));
            add(new Cell("Op"));
            add(new Cell("Fi"));
            add(new Cell("Fj"));
            add(new Cell("Fk"));
            add(new Cell("Qj"));
            add(new Cell("Qk"));
            add(new Cell("Rj"));
            add(new Cell("Rk"));
        }};
        List<List<Cell>> body2 = new ArrayList<>();
        for(int i=0;i<5;i++){
            List<Cell> element=new ArrayList<>();
            element.add(new Cell(fus[i].getName()));
            element.add(new Cell(String.valueOf(fus[i].getbusy())));
            element.add(new Cell(fus[i].getOp()));
            element.add(new Cell(fus[i].getFi()));
            element.add(new Cell(fus[i].getFj()));
            element.add(new Cell(fus[i].getFk()));
            element.add(new Cell(fus[i].getQj()));
            element.add(new Cell(fus[i].getQk()));
            element.add(new Cell(String.valueOf(fus[i].getRj())));
            element.add(new Cell(String.valueOf(fus[i].getRk())));
            body2.add(element);
        }
        new Table.ConsoleTable.ConsoleTableBuilder().addHeaders(header2).addRows(body2).build().print();
        System.out.println("结果寄存器状态表");
        List<Cell> header3 = new ArrayList<>();
        for (int i = 0; i <= 30; i += 2) {
            header3.add(new Cell("F" + i));
        }
        List<List<Cell>> body3 = new ArrayList<>();
        List<Cell> element=new ArrayList<>();
        for (int i = 0; i <= 30; i += 2) {
            element.add(new Cell(result.get("F"+i)));
        }
        body3.add(element);
        new Table.ConsoleTable.ConsoleTableBuilder().addHeaders(header3).addRows(body3).build().print();
        Scanner scanner=new Scanner(System.in);
        System.out.println("按回车键继续");
        scanner.nextLine();
    }
    //表格初始化方法
        public Instruction[] init1 ()
        {
            jf.setTitle("ScoreBoard");
            jf.setSize(1600, 800);
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setLocationRelativeTo(null);
            jf.setLayout(new GridLayout(3, 1,20,20));
            JLabel addLabel = new JLabel("加法部件延迟:");
            addLabel.setFont(new Font("宋体", Font.BOLD, 18));
            JTextField add=new JTextField();
            add.setMaximumSize(new Dimension(100, 50));
            JLabel multLabel = new JLabel("乘法部件延迟:");
            multLabel.setFont(new Font("宋体", Font.BOLD, 18));
            JTextField mult=new JTextField();
            mult.setMaximumSize(new Dimension(100, 50));
            JLabel divLabel = new JLabel("除法部件延迟:");
            divLabel.setFont(new Font("宋体", Font.BOLD, 18));
            JTextField div=new JTextField();
            div.setMaximumSize(new Dimension(100, 50));
            JLabel numberLabel = new JLabel("指令数:");
            numberLabel.setFont(new Font("宋体", Font.BOLD, 18));
            JTextField count = new JTextField();
            count.setMaximumSize(new Dimension(100, 50));
            JButton countButton = new JButton("确认");
            countButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addTime = Integer.parseInt(add.getText());
                    multTime = Integer.parseInt(mult.getText());
                    divTime = Integer.parseInt(div.getText());
                    number = Integer.parseInt(count.getText());
                }
            });
            box1 = Box.createHorizontalBox();
            box1.add(addLabel);
            box1.add(add);
            box1.add(multLabel);
            box1.add(mult);
            box1.add(divLabel);
            box1.add(div);
            box1.add(numberLabel);
            box1.add(count);
            box1.add(countButton);
            jf.add(box1);
            jf.setVisible(true);
            while(number==0){
            }
            Instruction[] instructions = new Instruction[number];
            JLabel textLabel = new JLabel("输入指令:");
            textLabel.setFont(new Font("宋体", Font.BOLD, 18));
            JTextArea textArea = new JTextArea(number, 20);
            textArea.setMaximumSize(new Dimension(800,200));
            JButton okButton = new JButton("确认");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String[] lines = textArea.getText().split("\n"); // 按换行符分隔文本
                    for (int i=0;i<lines.length;i++) {
                        Instruction inst=new Instruction(lines[i]);
                        instructions[i]=inst;
                        instructions[i].setInst(lines[i]);
                    }
                    synchronized (lock2) {
                        lock2.notify();
                    }
                }
            });
            box2=Box.createHorizontalBox();
            box2.add(textLabel);
            box2.add(textArea);
            box2.add(okButton);
            jf.add(box2);
            jf.setVisible(true);
            synchronized (lock2) {
                    try {
                        lock2.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
            }
            String[][] datas1 = new String[instructions.length][5];
            for (int i = 0; i < instructions.length; i++) {
                datas1[i][0]=instructions[i].getInst();
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
            box3 = Box.createHorizontalBox();
            box3.add(new JScrollPane(table1));
            box3.add(new JScrollPane(table2));
            box3.add(new JScrollPane(table3));
            box3.add(next);
            jf.add(box3);
            jf.setVisible(true);
            return instructions;
        }
        public Instruction[] init2(){
            Scanner scanner = new Scanner(System.in);
            System.out.print("请分别输入各部件的延迟，加法部件延迟：");
            addTime=scanner.nextInt();
            System.out.print("乘法部件延迟：");
            multTime=scanner.nextInt();
            System.out.print("除法部件延迟：");
            divTime=scanner.nextInt();
            // 获取输入的字符串数量
            System.out.print("请输入指令的数量：");
            number = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
            // 创建一个字符串数组
            Instruction[] instructions = new Instruction[number];
            System.out.println("请输入指令：");
            // 逐个获取输入的字符串
            for (int i = 0; i < number; i++) {
                String input = scanner.nextLine();
                instructions[i]=new Instruction(input);
                instructions[i].setInst(input);
            }
            return instructions;
         }
    }
