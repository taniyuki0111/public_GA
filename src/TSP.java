import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Random;
import javax.swing.*;
import java.awt.*;


//描画担当クラス
class DrawLines extends JFrame {
    //座標をx[]y[]の二つの配列で管理
    private static double[] x;
    private static double[] y;
    private static int width = 400;
    private static int height = 400;

    //遺伝子の並びを管理する配列rooting[]
    public static int[] rooting;

    //メインクラスが持つ座標データをコンストラクタで代入
    public DrawLines(double[] x, double[] y) {
        this.x = new double[x.length];
        this.y = new double[y.length];

        //描画範囲調整のために座標を調整
        double x_mean = 0;
        double y_mean = 0;
        for(int i=0;i<x.length;i++){
            x_mean += x[i];
            y_mean += y[i];
        }
        x_mean = x_mean/(x.length);
        y_mean = y_mean/(y.length);
        for(int i=0;i<x.length;i++){
            
            this.x[i] = ((x[i]-x_mean)/25+width/2);
            this.y[i] = ((y[i]-y_mean)/25+height/2);
            System.out.println(this.x[i]);
            System.out.println(this.y[i]);
        }
        
        initUI();
    }

    //ウィンドウの大きさ調整
    private void initUI() {
        setTitle("Draw Lines");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //描画用の一時座標にrootingを使ってアクセス     
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
   
    //メインクラスのrootingを代入、ウィンドウの表示
    public void exit(int[] rooting) {
        SwingUtilities.invokeLater(() -> {        
            this.rooting = rooting;
            
            setVisible(true);

            
        });
    }
}

//メインクラス
public class TSP {
    //再現性のために乱数を明示化
    static Random init_rand = new Random();
    static int seed = init_rand.nextInt();
    static Random rand = new Random(seed);

    //遺伝子の大きさ
    static int Max_num;
    //1世代あたりの個体数
    static int Num_ind = 20;
    //Elite数(0にすることは想定してないので注意)
    static int Elite_num = 2;
    //反復回数
    static int iteration = 2000;
    //突然変異率
    static double mutation_rate = 0.1;
    
    //個体管理用のあれこれ
    static int[][] individual;
    static int[][] childern;
    static double[] x;
    static double[] y;
    static int[][] Elite;
    
    public static void main(String[] args){
        //計算時間
        long startTime = System.nanoTime();

        //データ読み込み
        DataLoad();

        individual = new int[Num_ind][Max_num];
        childern = new int[Num_ind][Max_num];
        Elite = new int[Elite_num][Max_num];
        //描画用処理
        DrawLines drawLines = new DrawLines(x, y); 
        //最短経路用配列
        int[] rooting = new int[Max_num];

        //初期個体生成
        Generate_init();

        //初期個体の距離計算
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
        double avg = 0;
        try{
            FileWriter fw = new FileWriter(System.getProperty("user.dir")+"result_TSP.csv", false); 
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
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
                
                //保険用の致死遺伝子処理用メソッド
                //execution(dist);
                
                //10回ごとに個体表示
                if(roop%10==9)
                {
                    System.out.println("Generation"+(roop+1)+":");
                    for(int i=0;i<Num_ind;i++){
                    System.out.print(i+"  ");
                    for(int j=0;j<Max_num;j++){
                        System.out.print(individual[i][j]+",");
                    }   
                    System.out.print(":"+dist[i]);
                    System.out.println();
                }}

                //エリートをrootingに代入(優秀な遺伝子描画用)
                for(int z=0;z<Max_num;z++){
                    rooting[z] = Elite[0][z];
                }

                //最初にウィンドウ作成
                if(roop==0){
                    drawLines.exit(rooting);
                }
                //dist[0]（エリートの持つ距離）が最小値を更新したら実行
                else if(dist[0]<min){

                    // try {
                    //     Thread.sleep(50); // 描画が間に合わないときの調整
                    // } catch (InterruptedException e) {
                    // }
                    min = dist[0];
                    drawLines.repaint();
                }

                for(int j=0;j<dist.length;j++){
                    avg += dist[j];
                }
                avg = avg/dist.length;
                pw.println(avg+","+min);
            }
        
            pw.close();
        }catch(IOException e){
            System.out.println(e);
        }
        System.out.println("min:"+dist[0]);
        System.out.println("seed値:"+seed);

        long endTime = System.nanoTime();
        System.out.println("処理時間：" + (endTime - startTime) + " ナノ秒");
        
    }

    //突然変異
    public static void mutation(){
        int temp = 0;
        for(int i=0;i<Num_ind;i++){
            for(int j=0;j<Max_num;j++){
                if(rand.nextDouble()<mutation_rate){
                    temp = individual[i][j];
                    int ex_mutation = rand.nextInt(Max_num);
                    individual[i][j] = individual[i][ex_mutation];
                    individual[i][ex_mutation] = temp;
                }
            }
        }
    }

    //データ読み込み
    public static void DataLoad(){
        String filePath = "WesternSaharaPlot.txt";
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
            // for (int i = 0; i < lineCount; i++) {
            //     System.out.println("x[" + i + "] = " + x[i] + ", y[" + i + "] = " + y[i]);
            // }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //保険用の致死遺伝子処理用メソッド(基本使わない)
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

    //交叉処理
    public static void EXcross(int[][] combination){
        //進路候補
        int[][] course_cand = new int[Max_num][4];
        int[][] course_cand_sub = new int[Max_num][4];
        //未到達都市管理用
        int[] until_civ = new int[Max_num];
        int[] until_civ_2 = new int[Max_num];

        
        for(int i=0;i<combination.length;i++){
            
            //初期化
            for(int x=0;x<Max_num;x++){
                until_civ[x]=0;
                until_civ_2[x]=0;
                
            }

            //進路候補初期化
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
            
           
            //配列の値だけコピー
            for(int j=0;j<Max_num;j++){
                for(int k=0;k<4;k++){
                    course_cand_sub[j][k] = course_cand[j][k];
                }
            }
            
            //最初の要素を最短距離に設定
            double min_dist = Math.pow(x[course_cand[individual[combination[i][0]][0]][0]]-x[individual[combination[i][0]][0]],2)+Math.pow(y[course_cand[individual[combination[i][0]][0]][0]]-y[individual[combination[i][0]][0]],2);
            int course = course_cand[individual[combination[i][0]][0]][0];
            childern[i*2][0] = individual[combination[i][0]][0];
            until_civ[childern[i*2][0]] = -1;

            //2番目の要素を選択
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

            //1,2番目の要素無効化
            for(int j=0;j<Max_num;j++){
                
                for(int k=0;k<4;k++){
                    //一度通過した都市を無効化
                    if(childern[i*2][0] == course_cand[individual[combination[i][0]][j]][k] || childern[i*2][1] == course_cand[individual[combination[i][0]][j]][k]){      
                        course_cand[individual[combination[i][0]][j]][k] = -1;
                    }
                    
                }
            }
            
            //j番目の要素選択
            for(int j=1;j<Max_num-1;j++){
                //無効化された都市を飛ばすための変数
                int checkpoint = 0;
                //隣接都市に行ける都市がないときよう変数
                int flag = 0;

                //候補中に行ける都市があるか
                for(int k=0;k<4;k++){
                    if(course_cand[childern[i*2][j]][k] != -1){
                        course = course_cand[childern[i*2][j]][k];
                        checkpoint = k;
                        min_dist = Math.pow(x[childern[i*2][j]]-x[course_cand[childern[i*2][j]][k]],2)+Math.pow(y[childern[i*2][j]]-y[course_cand[childern[i*2][j]][k]],2);
                        break;
                    }
                    flag++;
                    
                }
                
                
                //無視された都市を飛ばして
                for(int k=checkpoint;k<4;k++){
                    if(course_cand[childern[i*2][j]][k] == -1){
                        continue;
                    }
                    double distance = Math.pow(x[childern[i*2][j]]-x[course_cand[childern[i*2][j]][k]],2)+Math.pow(y[childern[i*2][j]]-y[course_cand[childern[i*2][j]][k]],2);
                    //同じ距離のものが2つ以上あれば50%の確率で移行
                    if (distance <= min_dist){
                        if(distance == min_dist&&flag<2){
                            if(rand.nextDouble() > 0.5){
                                continue;
                            }
                        }
                        min_dist = distance;
                        course = course_cand[childern[i*2][j]][k];
                        // System.out.println(course);
                    }
                    
                    
                }
                childern[i*2][j+1] = course;
                until_civ[childern[i*2][j+1]] = -1;

                //隣接都市に行けなければ
                if(flag > 3){
                    double min_dist_until =  Double.MAX_VALUE;
                    for(int k=0;k<Max_num;k++){
                        if(until_civ[k]!=-1){
                            if(Math.pow(x[childern[i*2][j]]-x[until_civ[k]],2)+Math.pow(y[childern[i*2][j]]-y[until_civ[k]],2)<min_dist_until){
                                min_dist_until = Math.pow(x[childern[i*2][j]]-x[until_civ[k]],2)+Math.pow(y[childern[i*2][j]]-y[until_civ[k]],2);
                                course = k;
                            }
                        }
                    }
                    childern[i*2][j+1] = course;
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
                        if(distance == min_dist&&flag<2){
                            if(rand.nextDouble() > 0.5){
                                continue;
                            }
                        }
                        
                        min_dist = distance;
                        course = course_cand_sub[childern[i*2+1][j]][k];
                        
                        
                    }
                    
                    
                }   
                childern[i*2+1][j+1] = course;
                until_civ_2[childern[i*2+1][j+1]] = -1;
                

                if(flag > 3){
                    double min_dist_until =  Double.MAX_VALUE;
                    
                    
                    for(int k=0;k<Max_num;k++){
                        
                        if(until_civ_2[k]!=-1){
                            if(Math.pow(x[childern[i*2+1][j]]-x[until_civ_2[k]],2)+Math.pow(y[childern[i*2+1][j]]-y[until_civ_2[k]],2)<min_dist_until){
                                min_dist_until = Math.pow(x[childern[i*2+1][j]]-x[until_civ_2[k]],2)+Math.pow(y[childern[i*2+1][j]]-y[until_civ_2[k]],2);
                                
                                course = k;
                            }
                        }
                    }
                    childern[i*2+1][j+1] = course;
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

            
            
        }
        for(int i=0;i<Num_ind;i++){
            for(int j=0;j<Max_num;j++){
                individual[i][j] = childern[i][j];
            }
        }
    }


    //親個体選択
    public static int[][] choice_ind(double[] dist){
        int[][] combination =  new int[Num_ind/2+Num_ind%2][2];
        //親の確率配分
        double[] probability_table = new double[Num_ind];
        double sum_table = 0;
        for(int i=0;i<Num_ind;i++){
            probability_table[i] = dist[i]/10000;
            probability_table[i] = sigmoid(probability_table[i]);
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
                    break;
                }
            }
            threshold = 0;
            for(int j=0;j<probability_table.length;j++){
                threshold +=probability_table[j];
                if(threshold>seed2){
                    combination[i][1] = j;
                    break;
                }
            }
        }
        
                    
                    

        return combination;
    }

    //Elite選択
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

        
        for (int i = 0; i < Elite_num; i++) {
            //System.out.println(Elite_cand[i]);
            for(int j=0;j<Max_num;j++){
                Elite[i][j] = individual[Elite_cand[i]][j];
            }
        }

    }

    //Eliteで個体を上から書き換え
    public static void Elite_integrate(){
        for(int i=0;i<Elite_num;i++){
            for(int j=0;j<Max_num;j++){
                individual[i][j] = Elite[i][j];
            }
        }
        
    }

    //距離計算
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

    //sigmoid関数
    public static double sigmoid(double input){
        return 1/(1+Math.exp(-input));
    }


    //初期個体生成
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
 