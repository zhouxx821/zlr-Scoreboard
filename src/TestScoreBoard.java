import java.awt.*;

public class TestScoreBoard {
    public static void main(String[] args) throws AWTException {
        boolean flag=true;
        if(!test01()||!test02()||!test03()||!test04()||!test05()){
            flag=false;
        }
        if(flag) {
            System.out.println("模拟器输出正确");
        }
        if(!flag){
            System.out.println("模拟器输出错误");
        }
    }
    public static boolean test01() throws AWTException {
        String[] input={"LD F6,34(R2)",
                "LD F2,45(R3)",
                "MULTD F0,F2,F4",
                "SUBD F8,F6,F2",
                "DIVD F10,F0,F6",
                "ADDD F6,F8,F2"};
        int[][] cycle={{1,2,3,4},{5,6,7,8},{6,9,19,20},{7,9,11,12},{8,21,61,62},{13,14,16,22}};
        Instruction[] instructions=new Instruction[6];
        for (int i=0;i<input.length;i++) {
            instructions[i]=new Instruction(input[i]);
            instructions[i].setInst(input[i]);
        }
        ScoreBoard scoreBoard=TestScoreBoard.initScoreboard(instructions);
        scoreBoard.algorithm(instructions,"");
        return judge(instructions,cycle);
    }
    public static boolean test02() throws AWTException {
        String[] input={"ADDD F2,F6,F4", "LD F2,45(R3)"};
        int[][] cycle={{1,2,4,5},{6,7,8,9}};
        Instruction[] instructions=new Instruction[2];
        for (int i=0;i<input.length;i++) {
            instructions[i]=new Instruction(input[i]);
            instructions[i].setInst(input[i]);
        }
        ScoreBoard scoreBoard=TestScoreBoard.initScoreboard(instructions);
        scoreBoard.algorithm(instructions,"");
        return judge(instructions,cycle);
    }
    public static boolean test03() throws AWTException {
        String[] input={"MULTD F0,F2,F4",
                "MULTD F6,F8,F0",
                "ADDD F0,F8,F2"};
        int[][] cycle={{1,2,12,13},{2,14,24,25},{14,15,17,18}};
        Instruction[] instructions=new Instruction[3];
        for (int i=0;i<input.length;i++) {
            instructions[i]=new Instruction(input[i]);
            instructions[i].setInst(input[i]);
        }
        ScoreBoard scoreBoard=TestScoreBoard.initScoreboard(instructions);
        scoreBoard.algorithm(instructions,"");
        return judge(instructions,cycle);
    }
    public static boolean test04() throws AWTException {
        String[] input={"LD F6,34(R2)",
                "LD F6,45(R3)",
                "ADDD F0,F2,F6"};
        int[][] cycle={{1,2,3,4},{5,6,7,8},{6,9,11,12}};
        Instruction[] instructions=new Instruction[3];
        for (int i=0;i<input.length;i++) {
            instructions[i]=new Instruction(input[i]);
            instructions[i].setInst(input[i]);
        }
        ScoreBoard scoreBoard=TestScoreBoard.initScoreboard(instructions);
        scoreBoard.algorithm(instructions,"");
        return judge(instructions,cycle);
    }
    public static boolean test05() throws AWTException {
        String[] input={"MULTD F0,F2,F4",
                "DIVD F8 F4 F6",
                "MULTD F6,F8,F0",
                "ADDD F0,F10,F2"};
        int[][] cycle={{1,2,12,13},{2,3,43,44},{3,45,55,56},{14,15,17,46}};
        Instruction[] instructions=new Instruction[4];
        for (int i=0;i<input.length;i++) {
            instructions[i]=new Instruction(input[i]);
            instructions[i].setInst(input[i]);
        }
        ScoreBoard scoreBoard=TestScoreBoard.initScoreboard(instructions);
        scoreBoard.algorithm(instructions,"");
        return judge(instructions,cycle);
    }
    public static ScoreBoard initScoreboard(Instruction[] instructions){
        ScoreBoard scoreBoard=new ScoreBoard();
        scoreBoard.setAddTime(2);
        scoreBoard.setMultTime(10);
        scoreBoard.setDivTime(40);
        return scoreBoard;
    }
    public static boolean judge(Instruction[] instructions,int[][] cycle){
        for(int i=0;i<instructions.length;i++){
            if(instructions[i].getIssue()!=cycle[i][0]||instructions[i].getRead()!=cycle[i][1]||instructions[i].getExe()!=cycle[i][2]||instructions[i].getWriteback()!=cycle[i][3]){
                return false;
            }
        }
        return true;
    }
}
