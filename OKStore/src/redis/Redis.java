package redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class Redis {

	public static void main(String[] args) {
		Jedis jedis = new Jedis();
//		System.out.println("redis ping: " + jedis.ping());
//		System.out.println("redis info: " + jedis.info());
//		System.out.println("redis list push one two: " + jedis.lpush("one", "two")); // sysout: 1
//		System.out.println("redis list pop one: " + jedis.lpop("one")); // sysout: two
//		System.out.println("redis list pop one: " + jedis.lpop("one")); // sysout: null
//		System.out.println("user.key: " + jedis.get("user.key")); // sysout: null
//		System.out.println("user.key: " + jedis.incr("user.key")); // sysout: null
//		System.out.println("user.key: " + jedis.get("user.key")); // sysout: null
//		System.out.println("buy:" + jedis.incrByFloat("buy", -3.5d));
//		if (jedis.zrevrange("buy.s", 0, -1).size() > 0)
//			System.out.println(jedis.zrevrange("buy.s", 0, -1).iterator().next());

		// categories with the most previews
//		String previewByUserKey = "preview.s";
//		if (jedis.zrevrange(previewByUserKey, 0, -1).size() > 0) {
//			Integer categoryIdPreview = Integer.valueOf(jedis.zrevrange(previewByUserKey, 0, 0).iterator().next());
////			System.out.println(categoryIdPreview);
//			Double bestScorePreview = jedis.zscore(previewByUserKey, categoryIdPreview.toString());
////			System.out.println(bestScorePreview);
//			Set<String> bestCategoriesPreview = jedis.zrevrangeByScore(previewByUserKey, bestScorePreview, bestScorePreview);
//			System.out.println(bestCategoriesPreview);
//			
//		}

//		String keyBuy = "buy.s";
//		String keyPreview = "preview.s";
//		String keyLast = "last.s";
//		System.out.println(getCategoriesForArticleSuggestion(keyBuy, keyPreview, keyLast));
		
	}

	/**
	 * Gets set with categories by which article suggestions are made to the user.
	 * 
	 * @param jedis connection to redis db
	 * @param keyBuy
	 * @param keyPreview
	 * @param keyLast
	 * @return
	 * @throws Exception 
	 */
	public static Set<String> getCategoriesForArticleSuggestion(String keyBuy, String keyPreview, String keyLast) throws Exception {
		Set<String> bestCategories = new HashSet<String>();

		// categories with the most money spent
		bestCategories.addAll(getCategoriesByRevRange(keyBuy));

		// categories with the most previews
		bestCategories.addAll(getCategoriesByRevRange(keyPreview));

		// categories with the last buy
		bestCategories.addAll(getCategoriesForLastBuy(keyLast));
		
		return bestCategories;
	}

	/**
	 * Gets a set of categories for the categories the most money is spent in or the most article previews are done
	 * 
	 * @param jedis connection to redis db
	 * @param key by which to find sorted set
	 * @return return set with categories
	 * @throws Exception 
	 */
	public static Set<String> getCategoriesByRevRange(String key) throws Exception {
		Jedis jedis = new Jedis();
		Set<String> bestCategories = new HashSet<String>();

		if (jedis.zrevrange(key, 0, -1).size() > 0) {
			Integer categoryId = Integer.valueOf(jedis.zrevrange(key, 0, 0).iterator().next());
			Double bestScorePreview = jedis.zscore(key, categoryId.toString());
			bestCategories = jedis.zrevrangeByScore(key, bestScorePreview, bestScorePreview);
		}
		jedis.close();
		return bestCategories;
	}

	/**
	 * Gets a set of categories where the last buy was made.
	 * 
	 * @param jedis connection to redis db
	 * @param key by withc to find last buy category
	 * @return set with categories
	 */
	public static Set<String> getCategoriesForLastBuy(String key) {
		Jedis jedis = new Jedis();
		Set<String> bestCategories = new HashSet<String>();
		
		String jedisGet = jedis.get(key);
		if (jedisGet != null && jedisGet.length() > 0) {
			String lastCategories = jedisGet.replaceAll(" ", "");
			String[] sArray = lastCategories.substring(1, lastCategories.length() - 1).split(",");
			List<String> list = Arrays.asList(sArray);
			bestCategories = new HashSet<String>(list);
		}
		jedis.close();
		return bestCategories;
	}
}
