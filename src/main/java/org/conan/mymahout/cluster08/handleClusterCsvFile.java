package org.conan.mymahout.cluster08;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class handleClusterCsvFile {

    private static Logger logger = Logger.getLogger(handleClusterCsvFile.class);
    /**
     * 导出csv格式的数据，里面仅包含聚类名字和聚类内的点：
     * 
     * @throws IOException
     */
    private static void splitFile(String inFile) throws IOException {
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            String outFile;
            br = new BufferedReader(new FileReader(new File(inFile)));
            int i = 1;
            outFile = "datafile/clusterCsvFile/cluster" + i + ".csv";
            pw = new PrintWriter(new FileWriter(new File(outFile)));
            String s = null;
            while ((s = br.readLine()) != null) {
                outFile = "datafile/clusterCsvFile/cluster" + i + ".csv";
                pw = new PrintWriter(new FileWriter(new File(outFile)));
                String[] cols = s.split(",");
                for (int j = 1; j <= cols.length - 1; j++) {
                    String[] cell = cols[j].split("_");
                    StringBuffer sb = new StringBuffer();
                    sb.append(cell[2]);
                    sb.append(",");
                    sb.append(cell[4]);
                    pw.println(sb.toString());
                }
                if (pw != null) pw.close();
                if (logger.isInfoEnabled()) {
                    logger.info("完成文件");
                }
                i++;
            }
        } catch (Exception e) {
            if (br != null) br.close();
        }
    }

    public static void main(String[] args) throws IOException {
        // String inFile = "/data/mahout6/cluster-all.csv";
        String inFile = "datafile/clusterCsvFile/cluster-all.csv";
        splitFile(inFile);
    }
}
