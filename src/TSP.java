import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;
import java.util.function.BiFunction;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class DrawLines extends JFrame {
    private static double[] x;
    private static double[] y;
    public static int[] rooting;

    public DrawLines(double[] x, double[] y) {
        this.x = new double[x.length];
        this.y = new double[y.length];
        double x_mean = 0;
        double y_mean = 0;
        for(int i=0;i<x.length;i++){
            x_mean += x[i]/38;
            y_mean += y[i]/38;
        }
        x_mean = x_mean/(x.length*1.5);
        y_mean = y_mean/(y.length*2);
        for(int i=0;i<x.length;i++){
            this.x[i] = (x[i]/38)-x_mean;
            this.y[i] = (y[i]/38)-y_mean;
        }
        
        initUI();
    }

    private void initUI() {
        setTitle("Draw Lines");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }
    @Override
    public void paint(Graphics g) {
        
        super.paint(g);
        
        for (int i = 0; i < x.length - 1; i++) {
            int x1 = (int) x[rooting[i]];
            int y1 = (int) y[rooting[i]];
            int x2 = (int) x[rooting[i+1]];
            int y2 = (int) y[rooting[i+1]];
            g.drawLine(x1, y1, x2, y2);
        }
        int x1 = (int) x[rooting[rooting.length-1]];
        int y1 = (int) y[rooting[rooting.length-1]];
        int x2 = (int) x[rooting[0]];
        int y2 = (int) y[rooting[0]];
        g.drawLine(x1, y1, x2, y2);
            
    }
   

    public void updateDrawing(int[] rooting) {
        this.rooting = rooting;
        repaint();
    }

    public void exit(int[] rooting) {
        SwingUtilities.invokeLater(() -> {
          
            
            this.rooting = rooting;
            repaint();
            setVisible(true);

            
        });
    }
}

public class TSP {
    static Random init_rand = new Random();
    static int seed = init_rand.nextInt();
    static Random rand = new Random(seed);
    //遺伝子の大きさ
        static int Max_num;
        //1世代あたりの個体数
        static int Num_ind = 20;
        static int Elite_num = 3;
        static int iteration = 10000;
        
        static int[][] individual;
        static int[][] childern;
        static double[] x;
        static double[] y;
        static int[][] Elite;
    public static void main(String[] args){
        
        DataLoad();
        individual = new int[Num_ind][Max_num];
        childern = new int[Num_ind][Max_num];
        Elite = new int[Elite_num][Max_num];
        DrawLines drawLines = new DrawLines(x, y);
        
        int[] rooting = new int[Max_num];
        Generate_init();
        double dist[] = calc_dist();
        System.out.println("Generation0:");
        for(int i=0;i<Num_ind;i++){
            for(int j=0;j<Max_num;j++){
                System.out.print(individual[i][j]+",");
            }   
            System.out.print(":"+dist[i]);
            System.out.println();
        }
        double min = Double.MAX_VALUE;
        for(int roop=0;roop<iteration;roop++){
            
            int[][] combination = choice_ind(dist);;
        
            // for(int i=0;i<combination.length;i++){
            //     System.out.println(combination[i][0]+","+combination[i][1]);
            // }
            
            Elite_choice(dist);
            EXcross(combination);
            mutation();
            Elite_integrate();
            dist = calc_dist();
            execution(dist);
            
            if(roop%10 == 9)
            {
                System.out.println("Generation"+(roop+1)+":");
                for(int i=0;i<Num_ind;i++){
                
                for(int j=0;j<Max_num;j++){
                    System.out.print(individual[i][j]+",");
                }   
                System.out.print(":"+dist[i]);
                System.out.println();
            }}

                for(int z=0;z<Max_num;z++){
                    rooting[z] = Elite[0][z];
                }

                //drawLines.updateDrawing(rooting);
                
                // else{
                //     drawLines.kakikae(rooting);
                    
                    
                // }
            if(roop==0){
                drawLines.exit(rooting);
                
            }
            
            else if(dist[0]<min){
                min = dist[0];
                drawLines.repaint();
            }
            
    
                
                
            
            
        }
        
        
        System.out.println("min:"+dist[0]);
        System.out.println(seed);
    }

    public static void mutation(){
        int temp = 0;
        for(int i=0;i<Num_ind;i++){
            for(int j=0;j<Max_num;j++){
                if(rand.nextDouble()<0.2){
                    temp = individual[i][j];
                    int ex_mutation = rand.nextInt(Max_num);
                    individual[i][j] = individual[i][ex_mutation];
                    individual[i][ex_mutation] = temp;
                }
            }
        }
    }

    public static void DataLoad(){
        String filePath = "C:\\Users\\Owner\\Documents\\GitHub\\GeneticAlgoritm\\src\\WesternSaharaPlot.txt";
        try {
            Scanner scanner = new Scanner(new File(filePath));
            // ファイル内の行数を取得
            int lineCount = 0;
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                lineCount++;
            }
            
            Max_num =  lineCount;
            // 配列の初期化
            x = new double[lineCount];
            y = new double[lineCount];
            // スキャナを再初期化してファイルの先頭に戻す
            scanner.close();
            scanner = new Scanner(new File(filePath));
            // データを配列に格納
            for (int i = 0; i < lineCount; i++) {
                x[i] = scanner.nextDouble();
                y[i] = scanner.nextDouble();
            }
            // ファイルを閉じる
            scanner.close();
            // 配列の内容を表示（確認用）
            for (int i = 0; i < lineCount; i++) {
                System.out.println("x[" + i + "] = " + x[i] + ", y[" + i + "] = " + y[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void execution(double[] dist){
        for(int i= 0;i<Num_ind;i++){
            for (int j = 0; j < Max_num - 1; j++) {
                for (int k = j + 1; k < Max_num; k++) {
                    if (individual[i][j] == individual[i][k]) {
                        dist[i] = Double.MAX_VALUE; 
                    }
                }
            }
        }
    }

    public static void EXcross(int[][] combination){
        int[][] course_cand = new int[Max_num][4];
        int[][] course_cand_sub = new int[Max_num][4];
        int[] until_civ = new int[Max_num];
        int[] until_civ_2 = new int[Max_num];
        //候補数が3の場合は-1を代入するように
        for(int i=0;i<combination.length;i++){
            
            
            for(int x=0;x<Max_num;x++){
                until_civ[x]=0;
                until_civ_2[x]=0;
                
            }
            course_cand[individual[combination[i][0]][0]][0] = individual[combination[i][0]][Max_num-1];
            course_cand[individual[combination[i][0]][0]][1] = individual[combination[i][0]][1];
            for(int j=1;j<Max_num-1;j++){
                course_cand[individual[combination[i][0]][j]][0] =  individual[combination[i][0]][j-1];
                course_cand[individual[combination[i][0]][j]][1] =  individual[combination[i][0]][j+1];
            }
            course_cand[individual[combination[i][0]][Max_num-1]][0] =  individual[combination[i][0]][Max_num-2];
            course_cand[individual[combination[i][0]][Max_num-1]][1] =  individual[combination[i][0]][0];

            course_cand[individual[combination[i][1]][0]][2] = individual[combination[i][1]][Max_num-1];
            course_cand[individual[combination[i][1]][0]][3] = individual[combination[i][1]][1];
            for(int j=1;j<Max_num-1;j++){
                course_cand[individual[combination[i][1]][j]][2] =  individual[combination[i][1]][j-1];
                course_cand[individual[combination[i][1]][j]][3] =  individual[combination[i][1]][j+1];
            }
            course_cand[individual[combination[i][1]][Max_num-1]][2] =  individual[combination[i][1]][Max_num-2];
            course_cand[individual[combination[i][1]][Max_num-1]][3] =  individual[combination[i][1]][0];
            
            //進路確認用
            // if(i==1)
            // {
                
            //     for(int j=0;j<Max_num;j++){
            //     System.out.print("{");
            //     for(int l=0;l<4;l++){
            //         System.out.print(course_cand[j][l]+",");
            //     }
            //     System.out.println("}");
                
            // }}
           
            for(int j=0;j<Max_num;j++){
                for(int k=0;k<4;k++){
                    course_cand_sub[j][k] = course_cand[j][k];
                }
            }
            
            
            double min_dist = Math.pow(x[course_cand[individual[combination[i][0]][0]][0]]-x[individual[combination[i][0]][0]],2)+Math.pow(y[course_cand[individual[combination[i][0]][0]][0]]-y[individual[combination[i][0]][0]],2);
            int course = course_cand[individual[combination[i][0]][0]][0];
            childern[i*2][0] = individual[combination[i][0]][0];
            until_civ[childern[i*2][0]] = -1;
            for(int j=1;j<4;j++){
                
                double distance = Math.pow(x[course_cand[individual[combination[i][0]][0]][j]]-x[individual[combination[i][0]][0]],2)+Math.pow(y[course_cand[individual[combination[i][0]][0]][j]]-y[individual[combination[i][0]][0]],2);
                if (distance < min_dist){
                    min_dist = distance;
                    course = course_cand[individual[combination[i][0]][0]][j];
                }
            }
            childern[i*2][1] = course;
            until_civ[childern[i*2][1]] = -1;
            //System.out.println(i);
            for(int j=0;j<Max_num;j++){
                
                for(int k=0;k<4;k++){
                    
                    if(childern[i*2][0] == course_cand[individual[combination[i][0]][j]][k] || childern[i*2][1] == course_cand[individual[combination[i][0]][j]][k]){
                        
                        course_cand[individual[combination[i][0]][j]][k] = -1;
                    }
                    
                }
            }

            
            

            
            for(int j=1;j<Max_num-1;j++){
                int checkpoint = 0;
                int flag = 0;
                for(int k=0;k<4;k++){
                    if(course_cand[childern[i*2][j]][k] != -1){
                        course = course_cand[childern[i*2][j]][k];
                        checkpoint = k;
                        min_dist = Math.pow(x[childern[i*2][j]]-x[course_cand[childern[i*2][j]][k]],2)+Math.pow(y[childern[i*2][j]]-y[course_cand[childern[i*2][j]][k]],2);
                        break;
                    }
                    flag++;
                    
                }
                
                
                for(int k=checkpoint;k<4;k++){
                    if(course_cand[childern[i*2][j]][k] == -1){
                        continue;
                    }
                    double distance = Math.pow(x[childern[i*2][j]]-x[course_cand[childern[i*2][j]][k]],2)+Math.pow(y[childern[i*2][j]]-y[course_cand[childern[i*2][j]][k]],2);
                    
                    if (distance <= min_dist){
                        // if(distance == min_dist&&k==Max_num-1){
                        //     if(rand.nextDouble() > 0.5){
                        //         continue;
                        //     }
                        // }
                        min_dist = distance;
                        course = course_cand[childern[i*2][j]][k];
                        // System.out.println(course);
                    }
                    childern[i*2][j+1] = course;
                    
                }
                until_civ[childern[i*2][j+1]] = -1;
                if(flag > 3){
                    double min_dist_until =  Double.MAX_VALUE;
                    for(int k=0;k<Max_num;k++){
                        if(until_civ[k]!=-1){
                            if(Math.pow(x[childern[i*2][j]]-x[until_civ[k]],2)+Math.pow(y[childern[i*2][j]]-y[until_civ[k]],2)<min_dist_until){
                                min_dist_until = Math.pow(x[childern[i*2][j]]-x[until_civ[k]],2)+Math.pow(y[childern[i*2][j]]-y[until_civ[k]],2);
                                childern[i*2][j+1] = k;
                            }
                        }
                    }
                    until_civ[childern[i*2][j+1]] = -1;
                }

                for(int k=0;k<Max_num;k++){
                    for(int l=0;l<4;l++){
                        if(childern[i*2][j+1] == course_cand[k][l]){
                            course_cand[k][l] = -1;
                            
                        }
                        
                    }
                }


            }

            
            // for(int j=0;j<Max_num;j++){
            //     System.out.print(childern[i*2][j]);
                
            // }
            
            if(Num_ind%2 != 0&&i==combination.length-1) break;
            
            min_dist = Math.pow(x[course_cand_sub[individual[combination[i][1]][0]][0]]-x[individual[combination[i][1]][0]],2)+Math.pow(y[course_cand_sub[individual[combination[i][1]][0]][0]]-y[individual[combination[i][1]][0]],2);
            course = course_cand_sub[individual[combination[i][1]][0]][0];
            childern[i*2+1][0] = individual[combination[i][1]][0];
            
            until_civ_2[childern[i*2+1][0]] = -1;
            
            
            
            for(int j=1;j<4;j++){
                double distance = Math.pow(x[course_cand_sub[individual[combination[i][1]][0]][j]]-x[individual[combination[i][1]][0]],2)+Math.pow(y[course_cand_sub[individual[combination[i][1]][0]][j]]-y[individual[combination[i][1]][0]],2);
                if (distance < min_dist){
                    min_dist = distance;
                    course = course_cand_sub[individual[combination[i][1]][0]][j];
                }
            }
            childern[i*2+1][1] = course;
            until_civ_2[childern[i*2+1][1]] = -1;
            

            for(int j=0;j<Max_num;j++){
                for(int k=0;k<4;k++){
                    if(childern[i*2+1][0] == course_cand_sub[individual[combination[i][1]][j]][k] || childern[i*2+1][1] == course_cand_sub[individual[combination[i][1]][j]][k]){
                        course_cand_sub[individual[combination[i][1]][j]][k] = -1;
                    }
                    
                }
            }

            
            
            for(int j=1;j<Max_num-1;j++){
                
                int flag = 0;
                int checkpoint = 0;
                for(int k=0;k<4;k++){
                    if(course_cand_sub[childern[i*2+1][j]][k] != -1){
                        course = course_cand_sub[childern[i*2+1][j]][k];
                        checkpoint = k;
                        
                        min_dist = Math.pow(x[childern[i*2+1][j]]-x[course_cand_sub[childern[i*2+1][j]][k]],2)+Math.pow(y[childern[i*2+1][j]]-y[course_cand_sub[childern[i*2+1][j]][k]],2);
                        
                        break;

                    }
                    flag++;
                    
                }
                 
                
                for(int k=checkpoint;k<4;k++){
                    if(course_cand_sub[childern[i*2+1][j]][k] == -1)    continue;
                    
                    double distance = Math.pow(x[childern[i*2+1][j]]-x[course_cand_sub[childern[i*2+1][j]][k]],2)+Math.pow(y[childern[i*2+1][j]]-y[course_cand_sub[childern[i*2+1][j]][k]],2);
                    
                    
                    if (distance <= min_dist){
                        // if(distance == min_dist&&k==Max_num-1){
                        //     if(rand.nextDouble() > 0.5){
                        //         continue;
                        //     }
                        // }
                        
                        min_dist = distance;
                        course = course_cand_sub[childern[i*2+1][j]][k];
                        
                        
                    }
                    childern[i*2+1][j+1] = course;
                    
                }   
                until_civ_2[childern[i*2+1][j+1]] = -1;
                

                if(flag > 3){
                    double min_dist_until =  Double.MAX_VALUE;
                    
                    
                    for(int k=0;k<Max_num;k++){
                        
                        if(until_civ_2[k]!=-1){
                            if(Math.pow(x[childern[i*2+1][j]]-x[until_civ_2[k]],2)+Math.pow(y[childern[i*2+1][j]]-y[until_civ_2[k]],2)<min_dist_until){
                                min_dist_until = Math.pow(x[childern[i*2+1][j]]-x[until_civ_2[k]],2)+Math.pow(y[childern[i*2+1][j]]-y[until_civ_2[k]],2);
                                childern[i*2+1][j+1] = k;
                                
                            }
                        }
                    }
                    until_civ_2[childern[i*2+1][j+1]] = -1;
                }

                for(int k=0;k<Max_num;k++){
                    for(int l=0;l<4;l++){
                        if(childern[i*2+1][j+1] == course_cand_sub[k][l]){
                            course_cand_sub[k][l] = -1;
                            
                        }
                        
                    }
                }

                // for(int m=0;m<Max_num;m++){
                //     System.out.print("{");
                //     for(int l=0;l<4;l++){
                //         System.out.print(course_cand_sub[m][l]+",");
                //     }
                //     System.out.println("}");
                    
                // }
            
            }

            

            
            
        
            // System.out.println();
            // for(int j=0;j<Max_num;j++){
            //     System.out.print(childern[i*2+1][j]);
                
            // }
            // System.out.println();
            

            
            
            
        }
        for(int i=0;i<Num_ind;i++){
            for(int j=0;j<Max_num;j++){
                individual[i][j] = childern[i][j];
            }
        }


        
        
    }

    public static int[][] choice_ind(double[] dist){
        int[][] combination =  new int[Num_ind/2+Num_ind%2][2];
        //親の確率配分
        double[] probability_table = new double[Num_ind];
        double sum_table = 0;
        for(int i=0;i<Num_ind;i++){
            probability_table[i] = sigmoid(dist[i]);
            probability_table[i] = (1- probability_table[i]);
            sum_table += probability_table[i];
        }

        for(int i=0;i<Num_ind;i++){
            probability_table[i] = probability_table[i]/sum_table;
            //System.out.println(probability_table[i]);
        }

        
        for(int i=0;i<combination.length;i++){
            double seed = rand.nextDouble();
            double seed2 = rand.nextDouble();
            double threshold = 0;
            for(int j=0;j<probability_table.length;j++){
                threshold +=probability_table[j];
                if(threshold>seed){
                    combination[i][0] = j;
                    //System.out.println("aa"+seed);
                    // System.out.println(j);
                    //System.out.println(i+","+0+"  ,"+Now_Gen[combination[i][0]]);
                    break;
                }
            }
            threshold = 0;
            for(int j=0;j<probability_table.length;j++){
                threshold +=probability_table[j];
                if(threshold>seed2){
                    combination[i][1] = j;
                    //System.out.println("aa"+seed);
                    // System.out.println(j);
                    //System.out.println(i+","+0+"  ,"+Now_Gen[combination[i][0]]);
                    break;
                }
            }
        }
        
                    
                    

        return combination;
    }

    public static void Elite_choice(double[] dist){
        int[] Elite_cand = new int[Elite_num];
        
        // Elite_num回繰り返す
        for (int eliteIndex = 0; eliteIndex < Elite_num; eliteIndex++) {
            double minDist = Double.MAX_VALUE;
            int minDistIndex = -1;

            // distの中から最小値を見つける
            for (int i = 0; i < Num_ind; i++) {
                if (dist[i] < minDist) {
                    minDist = dist[i];
                    minDistIndex = i;
                }
            }

            // 最小値をElite_candに格納し、distから除外
            Elite_cand[eliteIndex] = minDistIndex;
            dist[minDistIndex] = Double.MAX_VALUE;  // 除外するために最大値で上書き
        }

        // 結果の出力
        //System.out.println("Elite_cand:");
        for (int i = 0; i < Elite_num; i++) {
            //System.out.println(Elite_cand[i]);
            for(int j=0;j<Max_num;j++){
                Elite[i][j] = individual[Elite_cand[i]][j];
            }
        }

        // for (int i = 0; i < Elite_num; i++) {
        //     System.out.println();
        //     for(int j=0;j<Max_num;j++){
        //         System.out.print(Elite[i][j]);
        //     }
        // }
        // System.out.println();

        
        
        
    }

    public static void Elite_integrate(){
        for(int i=0;i<Elite_num;i++){
            for(int j=0;j<Max_num;j++){
                individual[i][j] = Elite[i][j];
            }
        }
        
    }

    public static double[] calc_dist(){
        double[] dist= new double[Num_ind];
        for(int i=0;i<Num_ind;i++){
            for(int j = 0;j<Max_num-1;j++){
                dist[i] += Math.sqrt(Math.pow(x[individual[i][j]]-x[individual[i][j+1]],2)+Math.pow(y[individual[i][j]]-y[individual[i][j+1]],2));
                
            }

            dist[i] += Math.sqrt(Math.pow(x[individual[i][Max_num-1]]-x[individual[i][0]],2)+Math.pow(y[individual[i][Max_num-1]]-y[individual[i][0]],2));
            
            
        }
        
        return dist;
    }

    public static double sigmoid(double input){
        return 1/(1+Math.exp(-input));
    }


    public static void Generate_init(){
        int[] init_ind = new int[Max_num];
        for(int i=0;i<Max_num;i++){
            init_ind[i] = i;
        }

        shuffleArray(init_ind);

        for(int i=0;i<Num_ind;i++){
            for(int j=0;j<Max_num;j++){
                individual[i][j] = init_ind[j];
            }   
            shuffleArray(init_ind);
        }
    }

    private static void shuffleArray(int[] array) {
        // ランダムな値を生成するためのRandomクラスを作成
        

        // 配列をシャッフル
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            // 要素を交換
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }

    

    

    
}
 