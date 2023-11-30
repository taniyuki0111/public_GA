import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;
import java.util.function.BiFunction;

public class GeneticAlgolithm {
    static Random rand = new Random();
    //遺伝子の大きさ
        static int Max_num = 20;
        //1世代あたりの個体数
        static int Num_ind = 20;
        //何世代重ねるか
        static int Gen_num = 20000;
        //エリート個体数の大きさ
        static int[] Elite;
        //エリートの個体
        static String[] Elite_ind;

        static double[] fitness;

        static double Gen0_min = 0 ;
        static double Gen0_secmin = 9999;    
    public static void main(String[] args){
        Elite = new int[2];
        Elite_ind = new String[Elite.length];

        
        Long[] Gen0_Dec  = new Long[Num_ind];
        String[] Gen0 = new String[Num_ind];
        fitness = new double[Gen0.length];

        //Gen0作成
        for(int i=0;i<Num_ind;i++){
            Gen0_Dec[i] = GenerateLong(0,(long)(Math.pow(2,Max_num)));
            Gen0[i] = Long.toBinaryString(Gen0_Dec[i]);
            if(Gen0[i].length()<Max_num){
                String anaume = "";
                for(int j=0;j<Max_num-Gen0[i].length();j++){
                    anaume = anaume+"0";
                }
                Gen0[i] = anaume + Gen0[i];
            }
            
        }

        //評価
        System.out.println("Gen0");

        if(Num_ind%2==0) even_process(Gen0);
        else odd_process(Gen0);    
    }

    public static double sigmoid(double input){
        return 1/(1+Math.exp(-input));
    }

    public static void even_process(String[] Gen0){
        try{
            FileWriter fw = new FileWriter(System.getProperty("user.dir")+"result.csv", false); 
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            double Agroup =0;
            double Bgroup =0;

            for(int i=0;i<Gen0.length;i++){
                Agroup =0;
                Bgroup =0;
                for(int j=0;j<Gen0[i].length();j++){
                    if(Gen0[i].charAt(Gen0[i].length()-1-j) != '0'){
                        Agroup += Math.sqrt(j+1);
                    }
                    else{
                        Bgroup += Math.sqrt(j+1);
                    }
                }
                fitness[i] = Math.abs(Agroup-Bgroup);
                if(i==0){
                    Gen0_min = fitness[i];
                    Elite_ind[0] = Gen0[i];
                } 
                
                if(Gen0_min>fitness[i]){
                    if(i!=0){
                        Gen0_secmin = Gen0_min;
                        Elite_ind[1] = Elite_ind[0];
                    }
                    
                    Gen0_min = fitness[i];
                    
                    
                    Elite_ind[0] = Gen0[i];
                    
                }
                else if(Gen0_secmin>fitness[i]&&i!=0){
                    Gen0_secmin = fitness[i];
                    Elite_ind[1] = Gen0[i];
                }

                System.out.println(i+") "+Gen0[i]+"   fitness)"+fitness[i]);
                // System.out.println("Elite0:"+Elite_ind[0]);
                // System.out.println("Elite1:"+Elite_ind[1]);
            }

            

            String[] Now_Gen = Gen0;
            
            

            for(int roop=1;roop<Gen_num+1;roop++){
                if(roop%100==0)System.out.println("---------------------------------------------------------------");
                if(roop%100==0)System.out.println("Gen"+(roop));
                double[] probability_table = new double[Num_ind];
                double max = 0;
                double min = 0;
                double avg = 0;
                double secmin = 999999;
                int mutation_count = 0;
                double mutation = 0.05+((double)(roop/100)/100);
                
                
    
                

                int[][] combination = new int[Num_ind/2][2];
                //親の確率配分
                double sum_table = 0;
                for(int i=0;i<Num_ind;i++){
                    probability_table[i] = sigmoid(fitness[i]);
                    probability_table[i] = 0.15+(1- probability_table[i]);
                    sum_table += probability_table[i];
                }
                

                
                for(int i=0;i<Num_ind;i++){
                    probability_table[i] = probability_table[i]/sum_table;
                    //System.out.println(probability_table[i]);
                }

                //Elite戦略の時はiを増やす(Eliteが2体の時はi=1)
                for(int i=Elite.length/2;i<Num_ind/2;i++){
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
                            //System.out.println("bb"+seed2);
                            //System.out.println(i+","+1+"  ,"+Now_Gen[combination[i][1]]);
                            break;
                        }
                    }
                    
                }
                int[] closs_point = new int[combination.length];
                // Now_Gen[0] = Elite_ind[0];
                // Now_Gen[1] = Elite_ind[0];
                // for(int i= 0;i<combination.length;i++){
                //     System.out.println(i+","+0+","+Now_Gen[combination[i][0]]);
                //     System.out.println(i+","+1+","+Now_Gen[combination[i][1]]);
                // }

                for(int i=1;i<combination.length;i++){
                    closs_point[i] = rand.nextInt(Max_num);
                    Now_Gen[i*2] = Now_Gen[combination[i][0]].substring(0,closs_point[i])+Now_Gen[combination[i][1]].substring(closs_point[i],Max_num);
                    Now_Gen[i*2+1] = Now_Gen[combination[i][1]].substring(0,closs_point[i])+Now_Gen[combination[i][0]].substring(closs_point[i],Max_num);
                    for(int j=0;j<Max_num;j++){
                        if(rand.nextDouble()<mutation){
                            if(Now_Gen[i*2].charAt(j) == '0'){
                                //指定された文字列を文字アレイに変換します
                                char[] chars = Now_Gen[i*2].toCharArray();
                                //charアレイの指定された位置の文字を置き換えます
                                chars[j] = '1';
                                //文字アレイを文字列に変換し直します
                                Now_Gen[i*2] = String.valueOf(chars);
                            } 
                            else{
                                char[] chars = Now_Gen[i*2].toCharArray();
                                chars[j] = '0';
                                Now_Gen[i*2] = String.valueOf(chars);
                            }
                            mutation_count +=1;
                            //突然変異確認用
                            //System.out.println(i*2+","+j);
                        }
                    }

                    for(int j=0;j<Max_num;j++){
                        if(rand.nextDouble()<mutation){
                            if(Now_Gen[i*2+1].charAt(j) == '0'){
                                //指定された文字列を文字アレイに変換します
                                char[] chars = Now_Gen[i*2+1].toCharArray();
                                //charアレイの指定された位置の文字を置き換えます
                                chars[j] = '1';
                                //文字アレイを文字列に変換し直します
                                Now_Gen[i*2+1] = String.valueOf(chars);
                            } 
                            else{
                                char[] chars = Now_Gen[i*2+1].toCharArray();
                                chars[j] = '0';
                                Now_Gen[i*2+1] = String.valueOf(chars);
                            }
                            mutation_count +=1;
                            //System.out.println(i*2+1+","+j);
                        }
                    }
                }

                Now_Gen[0] = Elite_ind[0];
                Now_Gen[1] = Elite_ind[1];

                if(roop>Gen_num/2){
                    Long temp = GenerateLong(0,(long)(Math.pow(2,Max_num)));
                    Now_Gen[Num_ind-1] = Long.toBinaryString(temp);
                    if(Now_Gen[Num_ind-1].length()<Max_num){
                        String anaume = "";
                        for(int j=0;j<Max_num-Now_Gen[Num_ind-1].length();j++){
                            anaume = anaume+"0";
                        }
                        Now_Gen[Num_ind-1] = anaume + Now_Gen[Num_ind-1];
                    }
                }

                if(roop>Gen_num/4*3){
                    Long temp = GenerateLong(0,(long)(Math.pow(2,Max_num)));
                    Now_Gen[Num_ind-2] = Long.toBinaryString(temp);
                    if(Now_Gen[Num_ind-2].length()<Max_num){
                        String anaume = "";
                        for(int j=0;j<Max_num-Now_Gen[Num_ind-2].length();j++){
                            anaume = anaume+"0";
                        }
                        Now_Gen[Num_ind-2] = anaume + Now_Gen[Num_ind-2];
                    }
                }

                for(int i=0;i<Now_Gen.length;i++){
                    Agroup =0;
                    Bgroup =0;
                    for(int j=0;j<Now_Gen[i].length();j++){
                        if(Now_Gen[i].charAt(Now_Gen[i].length()-1-j) != '0'){
                            Agroup += Math.sqrt(j+1);
                        }
                        else{
                            Bgroup += Math.sqrt(j+1);
                        }
                    }
                    if(i==0){
                        max = fitness[i];
                        min = fitness[i];
                        Elite_ind[0] = Now_Gen[i];
                    }
                    
                    fitness[i] = Math.abs(Agroup-Bgroup);
                    avg += fitness[i];
                    if(fitness[i]>max) max = fitness[i];
                    else if(fitness[i]<min){
                        secmin = min;
                        min = fitness[i];
                        Elite_ind[1] = Elite_ind[0];
                        Elite_ind[0] = Now_Gen[i];
                    }
                    else if(fitness[i]<secmin){
                        secmin = fitness[i];
                        Elite_ind[1] = Now_Gen[i];
                    }
                    if(roop%100==0)System.out.println("("+combination[i/2][0]+","+combination[i/2][1]+")   "+closs_point[i/2]+":    "+i+") "+Now_Gen[i]+"   fitness)"+fitness[i]);
                    
                }
                avg = avg/Num_ind;
                

                pw.println(avg+","+min);
                    

                    
                
                if(roop%100==0) System.out.println("note: Generation"+(roop)+"  :\nmax = "+max+"\nmin="+min+"\navg="+avg+"\nmutation="+mutation_count);
            }
            pw.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void odd_process(String[] Gen0){
        try{
            FileWriter fw = new FileWriter(System.getProperty("user.dir")+"result.csv", false); 
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            for(int i=0;i<Gen0.length;i++){
                double Agroup =0;
                double Bgroup =0;
                for(int j=0;j<Gen0[i].length();j++){
                    if(Gen0[i].charAt(Gen0[i].length()-1-j) != '0'){
                        Agroup += Math.sqrt(j+1);
                    }
                    else{
                        Bgroup += Math.sqrt(j+1);
                    }
                }
                fitness[i] = Math.abs(Agroup-Bgroup);
                if(i==0){
                    Gen0_min = fitness[i];
                    Elite_ind[0] = Gen0[i];
                } 
                
                if(Gen0_min>fitness[i]){
                    if(i!=0){
                        Gen0_secmin = Gen0_min;
                        Elite_ind[1] = Elite_ind[0];
                    }
                    
                    Gen0_min = fitness[i];
                    
                    
                    Elite_ind[0] = Gen0[i];
                    
                }
                else if(Gen0_secmin>fitness[i]&&i!=0){
                    Gen0_secmin = fitness[i];
                    Elite_ind[1] = Gen0[i];
                }

                System.out.println(i+") "+Gen0[i]+"   fitness)"+fitness[i]);
                // System.out.println("Elite0:"+Elite_ind[0]);
                // System.out.println("Elite1:"+Elite_ind[1]);
            }

            

            String[] Now_Gen = Gen0;
            
            

            for(int roop=1;roop<Gen_num+1;roop++){
                System.out.println("---------------------------------------------------------------");
                System.out.println("Gen"+(roop+1));
                double[] probability_table = new double[Num_ind];
                double max = 0;
                double min = 0;
                double avg = 0;
                double secmin = 999999;
                int mutation_count = 0;
                double mutation = 0.05+((double)(roop/100)/100);
                //Eliteは偶数を想定
                

                int[][] combination = new int[Num_ind/2+1][2];
                //親の確率配分
                double sum_table = 0;
                for(int i=0;i<Num_ind;i++){
                    probability_table[i] = sigmoid(fitness[i]);
                    probability_table[i] = 0.15+(1- probability_table[i]);
                    sum_table += probability_table[i];
                }
                

                
                for(int i=0;i<Num_ind;i++){
                    probability_table[i] = probability_table[i]/sum_table;
                    //System.out.println(probability_table[i]);
                }

                //Elite戦略の時はiを増やす(Eliteが2体の時はi=1)
                for(int i=Elite.length/2;i<Num_ind/2+1;i++){
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
                int[] closs_point = new int[combination.length];

                for(int i=1;i<combination.length;i++){
                    closs_point[i] = rand.nextInt(Max_num);
                    Now_Gen[i*2] = Now_Gen[combination[i][0]].substring(0,closs_point[i])+Now_Gen[combination[i][1]].substring(closs_point[i],Max_num);
                    if(combination.length-1 != i) Now_Gen[i*2+1] = Now_Gen[combination[i][1]].substring(0,closs_point[i])+Now_Gen[combination[i][0]].substring(closs_point[i],Max_num);
                    for(int j=0;j<Max_num;j++){
                        if(rand.nextDouble()<mutation){
                            if(Now_Gen[i*2].charAt(j) == '0'){
                                //指定された文字列を文字アレイに変換します
                                char[] chars = Now_Gen[i*2].toCharArray();
                                //charアレイの指定された位置の文字を置き換えます
                                chars[j] = '1';
                                //文字アレイを文字列に変換し直します
                                Now_Gen[i*2] = String.valueOf(chars);
                            } 
                            else{
                                char[] chars = Now_Gen[i*2].toCharArray();
                                chars[j] = '0';
                                Now_Gen[i*2] = String.valueOf(chars);
                            }
                            mutation_count +=1;
                            //突然変異確認用
                            System.out.println(i*2+","+j);
                        }
                    }

                    if(combination.length-1 != i){
                        for(int j=0;j<Max_num;j++){
                            if(rand.nextDouble()<mutation){
                                if(Now_Gen[i*2+1].charAt(j) == '0'){
                                    //指定された文字列を文字アレイに変換します
                                    char[] chars = Now_Gen[i*2+1].toCharArray();
                                    //charアレイの指定された位置の文字を置き換えます
                                    chars[j] = '1';
                                    //文字アレイを文字列に変換し直します
                                    Now_Gen[i*2+1] = String.valueOf(chars);
                                } 
                                else{
                                    char[] chars = Now_Gen[i*2+1].toCharArray();
                                    chars[j] = '0';
                                    Now_Gen[i*2+1] = String.valueOf(chars);
                                }
                                mutation_count +=1;
                                System.out.println(i*2+1+","+j);
                            }
                        }
                    }
                    
                }

                Now_Gen[0] = Elite_ind[0];
                Now_Gen[1] = Elite_ind[1];

                if(roop>Gen_num/2){
                    Long temp = GenerateLong(0,(long)(Math.pow(2,Max_num)));
                    Now_Gen[Num_ind-1] = Long.toBinaryString(temp);
                    if(Now_Gen[Num_ind-1].length()<Max_num){
                        String anaume = "";
                        for(int j=0;j<Max_num-Now_Gen[Num_ind-1].length();j++){
                            anaume = anaume+"0";
                        }
                        Now_Gen[Num_ind-1] = anaume + Now_Gen[Num_ind-1];
                    }
                }

                if(roop>Gen_num/4*3){
                    Long temp = GenerateLong(0,(long)(Math.pow(2,Max_num)));
                    Now_Gen[Num_ind-2] = Long.toBinaryString(temp);
                    if(Now_Gen[Num_ind-2].length()<Max_num){
                        String anaume = "";
                        for(int j=0;j<Max_num-Now_Gen[Num_ind-2].length();j++){
                            anaume = anaume+"0";
                        }
                        Now_Gen[Num_ind-2] = anaume + Now_Gen[Num_ind-2];
                    }
                }

                for(int i=0;i<Now_Gen.length;i++){
                    double Agroup =0;
                    double Bgroup =0;
                    for(int j=0;j<Now_Gen[i].length();j++){
                        if(Now_Gen[i].charAt(Now_Gen[i].length()-1-j) != '0'){
                            Agroup += Math.sqrt(j+1);
                        }
                        else{
                            Bgroup += Math.sqrt(j+1);
                        }
                    }
                    if(i==0){
                        max = fitness[i];
                        min = fitness[i];
                        Elite_ind[0] = Now_Gen[i];
                    }
                    
                    fitness[i] = Math.abs(Agroup-Bgroup);
                    avg += fitness[i];
                    if(fitness[i]>max) max = fitness[i];
                    else if(fitness[i]<min){
                        secmin = min;
                        min = fitness[i];
                        Elite_ind[1] = Elite_ind[0];
                        Elite_ind[0] = Now_Gen[i];
                    }
                    else if(fitness[i]<secmin){
                        secmin = fitness[i];
                        Elite_ind[1] = Now_Gen[i];
                    }
                    System.out.println("("+combination[i/2][0]+","+combination[i/2][1]+")   "+closs_point[i/2]+":    "+i+") "+Now_Gen[i]+"   fitness)"+fitness[i]);
                }
                avg = avg/Num_ind;
                

                pw.println(avg+","+min);
                    

                    
            
                System.out.println("note: Generation"+(roop)+"  :\nmax = "+max+"\nmin="+min+"\navg="+avg+"\nmutation="+mutation_count);
            }
            pw.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static Long GenerateLong(long min,long max) {
        
        
        long randomLong = rand.nextLong();
        
        // 指定された範囲内の乱数を生成
        long result = min + (Math.abs(randomLong) % (max - min + 1));
        return result;

    }

    
}
    
