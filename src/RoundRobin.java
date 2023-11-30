import java.util.Random;
import java.util.function.BiFunction;

public class RoundRobin {

    public static void main(String[] args){
        int temp = 20;
        double dist=0;
        int[] best = new int[temp];
        int[] subbest = new int[temp];
        long startTime = System.nanoTime();
        for(int i=0;i<Math.pow(2,temp);i++){
            

            int[] N_sec = new int[temp];
            double Agroup = 0;
            double Bgroup = 0; 
            String test = Integer.toBinaryString(i);
            if(test.length()<temp){
                String anaume = "";
                for(int j=0;j<temp-test.length();j++){
                    anaume = anaume+"0";
                }
                test = anaume + test;
            }
            
            for(int j=0;j<test.length();j++){
                if(test.charAt(test.length()-1-j) != '0'){
                    N_sec[j] = 1;
                    Agroup += Math.sqrt(j+1);
                }
                else{
                    Bgroup += Math.sqrt(j+1);
                }
            }
            
            
            if(i==0){
                dist = Math.abs(Agroup-Bgroup);
            }

            
            if(Math.abs(Agroup-Bgroup) < dist){
                dist = Math.abs(Agroup-Bgroup);
                for(int j=0;j<temp;j++){
                    subbest[j] = best[j];
                    best[j] = N_sec[j];
                }
            }

            // for(int j=0;j<temp;j++){
            //     System.out.print(N_sec[temp-(j+1)]);
            // }
            // System.out.println("\n"+dist);
            

            


        }

        for(int i=0;i<temp;i++){
            System.out.print(best[temp-(i+1)]);
            
        }
        System.out.println("");
        for(int i=0;i<temp;i++){
            System.out.print(subbest[temp-(i+1)]);
            
        }
        System.out.println("\n"+dist);
        long endTime = System.nanoTime();
        System.out.println("処理時間：" + (endTime - startTime) + " ナノ秒");
            }
    
    }
    
