package org.conan.mymahout.recommendation.book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class BookFilterGenderResult {

    final static int NEIGHBORHOOD_NUM = 2;
    final static int RECOMMENDER_NUM = 3;

    public static void main(String[] args) throws TasteException, IOException {
    	long uid = 198;//65
    	String gender = "F";//"M"
    	String file = "datafile/book/rating.csv";
    	String fileF = filterRatingDataByUserGender(file, gender);
    	
        DataModel dataModel = RecommendFactory.buildDataModel(fileF);
        RecommenderBuilder rb1 = BookEvaluator.userEuclidean(dataModel);
        RecommenderBuilder rb2 = BookEvaluator.itemEuclidean(dataModel);
        RecommenderBuilder rb3 = BookEvaluator.userEuclideanNoPref(dataModel);
        RecommenderBuilder rb4 = BookEvaluator.itemEuclideanNoPref(dataModel);
        
        System.out.print("userEuclidean       =>");
        filterGender(uid, rb1, dataModel, gender);
        System.out.print("itemEuclidean       =>");
        filterGender(uid, rb2, dataModel, gender);
        System.out.print("userEuclideanNoPref =>");
        filterGender(uid, rb3, dataModel, gender);
        System.out.print("itemEuclideanNoPref =>");
        filterGender(uid, rb4, dataModel, gender);
    }

	/**
	 * 把rating中的数据先把所有男性的评分去除，生成过滤后 数据文件。
	 */
    private static String filterRatingDataByUserGender(String file, String gender) throws IOException {
    	String fileF = "datafile/book/rating" + gender + ".csv";
    	Set<Long> userids = getByGender("datafile/book/user.csv", gender);// 特定性别的用户ids
    	
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        PrintWriter pw = new PrintWriter(new FileWriter(new File(fileF)));
        String s = null;
        while ((s = br.readLine()) != null) {
            String[] cols = s.split(",");
            if (userids.contains( Long.parseLong(cols[0]) )) {
                pw.println(s);
            }
        }
        br.close();
        pw.close();
    	
		return fileF;
	}


	/**
     * 对用户性别进行过滤
     */        
    public static void filterGender(long uid, RecommenderBuilder recommenderBuilder, DataModel dataModel, String gender) throws TasteException, IOException {
        Set<Long> userids = getByGender("datafile/book/user.csv", gender);

        //计算指定性别用户打分过的图书
        Set<Long> bookids = new HashSet<Long>();
        for (long uids : userids) {
            LongPrimitiveIterator iter = dataModel.getItemIDsFromUser(uids).iterator();
            while (iter.hasNext()) {
                long bookid = iter.next();
                bookids.add(bookid);
            }
        }

        IDRescorer rescorer = new FilterRescorer(bookids);
        List<RecommendedItem> list = recommenderBuilder.buildRecommender(dataModel).recommend(uid, RECOMMENDER_NUM, rescorer);
        RecommendFactory.showItems(uid, list, false);
    }

	/**
	 * 获得男性或者女 性用户ID
	 * 
	 * @param gender
	 *            取值为 M 代表 男性 ， F代表女性
	 */
    public static Set<Long> getByGender(String file,String gender) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        Set<Long> userids = new HashSet<Long>();
        String s = null;
        while ((s = br.readLine()) != null) {
            String[] cols = s.split(",");
            if (cols[1].equals(gender)) {// 判断性别
                userids.add(Long.parseLong(cols[0]));
            }
        }
        br.close();
        return userids;
    }
    

}

/**
 * 对结果重计算
 */
class FilterRescorer implements IDRescorer {
    final private Set<Long> userids;

    public FilterRescorer(Set<Long> userids) {
        this.userids = userids;
    }

    @Override
    public double rescore(long id, double originalScore) {
        return isFiltered(id) ? Double.NaN : originalScore;
    }

    @Override
    public boolean isFiltered(long id) {
        return ! userids.contains(id);
    }
}
