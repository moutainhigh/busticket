package com.vpclub.bait.busticket.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpclub.bait.busticket.api.config.RedisConifg;
import com.vpclub.bait.busticket.api.handler.RedisLockHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

		@Resource
		RedisConifg redisConifg;
	    //jedis池
		private static JedisPool pool; 
		
		private static RedisLockHandler locker;

		@PostConstruct
		private void init() {
			JedisPoolConfig config = new JedisPoolConfig();
			int maxIdle = Integer.valueOf(redisConifg.getPool().getMaxIdle());
			int maxTotal = Integer.valueOf(redisConifg.getPool().getMaxTotal());
			boolean testOnBorrow = Boolean.valueOf(redisConifg.getPool().getTestOnBorrow());
			boolean onreturn = Boolean.valueOf(redisConifg.getPool().getTestOnReturn());
			int timeout = Integer.valueOf(redisConifg.getPool().getTimeout());

			String ip =  redisConifg.getIp();
			int port = Integer.valueOf(redisConifg.getPort());
			String password = redisConifg.getPassword();


			log.info("*******************redisConfig********************");
			log.info("ip=>" + ip + " port=>" + port + " password=>" + password);
			log.info("maxIdle=>" + maxIdle);
			log.info("maxTotal=>" + maxTotal);
			log.info("testOnBorrow=>" + testOnBorrow);
			log.info("onreturn=>" + onreturn);
			log.info("*******************redisConfig********************");

			//设置池配置项值
			config.setMaxIdle(maxIdle);
			config.setMaxTotal(maxTotal);
			config.setTestOnBorrow(testOnBorrow);
			config.setTestOnReturn(onreturn);

			//根据配置实例化jedis池
			if(StringUtils.isNotBlank(password)){
				pool = new JedisPool(config, ip, port, timeout, password);
			}else{
				pool = new JedisPool(config, ip, port, timeout);
			}
			log.info("redis连接池创建成功");

			locker = new RedisLockHandler(pool);
		}

		/**************Redis分布式锁***************/
		/**
		 * 获取锁 如果锁可用 立即返回true， 否则返回false，不等待
		 */
		public boolean tryLock(String key) {
			return tryLock(key, 0L, null);
		}

		/**
		 * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
		 */
		public boolean tryLock(String key, long timeout, TimeUnit unit) {
			boolean result = locker.tryLock(key, timeout, unit);
			if(result){
				log.info("Get tryLock=>" + key);
			}else{
				log.info("Not Get tryLock=>" + key);
			}
			return result;
		}
		
		/**
		 * 如果锁空闲立即返回 获取失败 一直等待
		 */
		public void lock(String key) {
			locker.lock(key);
			log.info("Get lock=>" + key);
		}
		
		/**
		 * 释放锁
		 */
		public void unLock(String key) {
			locker.unLock(key);
			log.info("unLock=>" + key);
		}
		
		/**
		 * 串行的执行操作
		 */
		
		/**************Redis分布式锁***************/
		
		/**
		 * 获取redis链接池
		 */
		public JedisPool redisPool(){
			return pool;
		}
		
		/**
		 * 添加数据到redis中
		 */
		public void redisSet(String key,String value){
			Jedis jedis = pool.getResource(); 
			jedis.set(key, value);
			log.info("Redis Set " + key + ":" + value);
			pool.returnResource(jedis);;
		}
		
		
		/**
		 * 添加数据到redis中
		 */
		public void redisSet(String key, int seconds, String value){
			Jedis jedis = pool.getResource(); 
			jedis.setex(key, seconds, value);
			log.info("Redis Setex " + key + ":" + seconds + ":" + value);
			pool.returnResource(jedis);;
		}
		
		/**
		 * 更新redis中的数据
		 */
		public void redisUpdate(String key, int seconds, String value){
			Jedis jedis = pool.getResource(); 
			jedis.del(key);
			jedis.setex(key, seconds, value);
			pool.returnResource(jedis);;
		}
		
		
		/**
		 * 从redis中删除数据
		 */
		
		public void redisDel(String key){
			Jedis jedis = pool.getResource(); 
			jedis.del(key);
			log.info("Redis Del " + key);
			pool.returnResource(jedis);;
		}
		
		/**
		 * 从redis中获取数据
		 */
		public String redisGet(String key){
			Jedis jedis = pool.getResource(); 
			String value = jedis.get(key);
			log.info("Redis Get " + key + ":" + value);
			pool.returnResource(jedis);
			return value;
		}
		
		/**
		 * 从redis中批量获取Key
		 */
		public Set<String> redisKeys(String pattarn){
			Jedis jedis = pool.getResource(); 
			Set<String> keys = jedis.keys(pattarn);
			pool.returnResource(jedis);
			return keys;
		}
		
		/**
		 * redis监听某个频道的消息
		 */
		public void subscribe(final JedisPubSub jedisPubSub,final String channel){
			new Thread(){
				public void run(){
					Jedis jedis = pool.getResource(); 
					jedis.subscribe(jedisPubSub, channel);
					pool.returnResource(jedis);
				}
			}.start();
		}
		
		/**
		 * 读取redis中的Key并生成对象
		 * @param key
		 * @param clazz
		 * @return
		 * @throws Exception
		 */
		public <T extends Object> T redisGetObj(String key,Class<T> clazz) throws Exception{
			T result = null;
			String value = redisGet(key);
			if(StringUtils.isNotBlank(value)){
				ObjectMapper om=new ObjectMapper();
				result=om.readValue(value, clazz);
			}
			return result;
		}
		
		/**
		 * 将对象以Json的格式放入Redis中
		 * @param key
		 * @param seconds
		 * @param object
		 * @throws Exception
		 */
		public void redisSetObj(String key, int seconds, Object object){
			try{
				if(object != null){
					ObjectMapper om=new ObjectMapper();
					redisSet(key, seconds, om.writeValueAsString(object));
				}
			}catch(Exception e){
				log.info("redisSetObj Exception " + key + ":" + object);
			}
			
		}
		
		/**
		 * 获取对象列表
		 * @param key
		 * @param clazz
		 * @return
		 * @throws Exception
		 */
		public <T extends Object> List<T> redisGetList(String key,Class<T> clazz){
			List<T> result = null;
			String value = redisGet(key);
			if(StringUtils.isNotBlank(value)){
				JSONArray array = JSONArray.fromObject(value);
				result = JSONArray.toList(array, clazz);
			}
			return result;
		}
		
		/**
		 * 锁住一个Key一段时间，锁定成功返回true，锁定失败返回false
		 * @param lockerKey   要锁住的唯一KEY
		 * @param lockerTime  要锁住的时间 
		 * @return
		 */
		public boolean lockKeyByTime(String lockerKey,int lockerTime){
			boolean flag = false;
			lock(lockerKey);
			String flagKey = lockerKey + "_FLAGKEY";
			String flagValue = redisGet(flagKey);
			if(StringUtils.isBlank(flagValue)){
				redisSet(flagKey, lockerTime, "1");
				flag = true;
			}
		    unLock(lockerKey);
			return flag;
		}
		
		/**
		 * 添加成员到排序集合中，member是唯一的，score可以重复，加入相同的menber只会更新score
		 * @param zsetName  排序集合的名字
		 * @param score     成员的分数值
		 * @param member    成员的值
		 * @return          1标识添加新成员成功，0标识menber已存在更新menber的分数值score
		 */
		public int zadd(String zsetName, double score, String member){
			Jedis jedis = pool.getResource(); 
			Long flag = jedis.zadd(zsetName,score,member);
			pool.returnResource(jedis);
			return flag.intValue();
		}
		
		/**
		 * 获取排序集合中指定范围的值集合(按分数值升序)
		 * @param zsetName   排序集合的名字
		 * @param start      范围开始index值
		 * @param stop       范围结束index值
		 * @return           制定范围的成员集合
		 */
		public Set<String> zrange(String zsetName, int start, int stop){
			Jedis jedis = pool.getResource(); 
			Set<String> setValues = jedis.zrange(zsetName,start,stop);
			pool.returnResource(jedis);
			return setValues;
		}
		
		/**删除制定集合中指定范围的值(按分数值升序）
		 * @param zsetName  排序集合的名字
		 * @param start     范围开始index值
		 * @param stop      范围结束index值
		 * @return          删除成员的个数
		 */
		public int zremrangeByRank(String zsetName, int start, int stop){
			Jedis jedis = pool.getResource(); 
			Long flag = jedis.zremrangeByRank(zsetName,start,stop);
			pool.returnResource(jedis);
			return flag.intValue();
		}
		
	    /**
	     * 判断key是否存在redis中
	     * @param key    
	     * @return
	     */
		public boolean exists(String key){
			Jedis jedis = pool.getResource(); 
			boolean ret = jedis.exists(key);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
	     * 为key设置超时时间
	     * @param key      键
	     * @param seconds  超时时间
	     * @return
	     */
		public long expire(String key,int seconds){
			Jedis jedis = pool.getResource(); 
			Long ret = jedis.expire(key, seconds);
			pool.returnResource(jedis);
			return ret.longValue();
		}
		
	    /**
	     * 往HashMap中添加键值对
	     * @param mapName     HashMap的名字
	     * @param hashKey     键
	     * @param hashValue   值
	     * @return  如果添加的键hashKey已存在返回0，添加失败不更新hashValue的值；
	     *          如果添加的键hashKey不存在返回1，添加成功将键值对添加到map中；
	     */
		public long hsetnx(String mapName,String hashKey,String hashValue){
			Jedis jedis = pool.getResource(); 
			Long ret = jedis.hsetnx(mapName, hashKey, hashKey);
			pool.returnResource(jedis);
			return ret.longValue();
		}
		
		/**
		 * 往HashMap中添加键值对
		 * @param mapName       HashMap的名字
		 * @param hashKey       键
		 * @param hashValue     值
		 * @return  如果键已存在，更新值为hashValue返回0
		 *          如果键不存在，创建新的键值对返回1
  		 */         
		public int hset(String mapName,String hashKey,String hashValue){
			Jedis jedis = pool.getResource(); 
			Long ret = jedis.hset(mapName, hashKey, hashValue);
			pool.returnResource(jedis);
			return ret.intValue();
		}
		
		/**
		 * 将javaHashMap的键只对添加到redis的mapName中
		 * @param mapName       redis中hashmap的名字
		 * @param javaHashMap   java  HashMap
		 * @return 操作成功返回OK,否则抛出异常
		 */
		public String hmset(String mapName,Map<String, String> javaHashMap){
			Jedis jedis = pool.getResource(); 
			String ret = jedis.hmset(mapName, javaHashMap);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
		 * 获取HashMap中对应键的值
		 * @param mapName   redis中hashmap的名字
		 * @param hashKey   键
		 * @return 值
		 */
		public String hget(String mapName,String hashKey){
			Jedis jedis = pool.getResource(); 
			String ret = jedis.hget(mapName, hashKey);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
		 * 获取redis中Map的所有键值对
		 * @param mapName   hashmap的名字
		 * @return
		 */
		public Map<String, String> hgetAll(String mapName){
			Jedis jedis = pool.getResource(); 
			Map<String, String> ret = jedis.hgetAll(mapName);
			pool.returnResource(jedis);
			return ret;
		}
		
	    /**
	     * 删除HashMap中添加键值对
	     * @param mapName     HashMap的名字
	     * @param hashKeys    键
	     * @return  如果键存在并且删除成功返回1；
	     *          如果键不存在返回0；
	     */
		public long hdel(String mapName, String... hashKeys){
			Jedis jedis = pool.getResource(); 
			Long ret = jedis.hdel(mapName, hashKeys);
			pool.returnResource(jedis);
			return ret.longValue();
		}
		
		/**
		 * 将key对应的offset的二进制位设为1
		 * @param key      键
		 * @param offset   二进制偏移位
		 * @param value    二进制位的值true标识1，false标识0
		 * @return
		 */
		public boolean setbit(String key, long offset, boolean value){
			Jedis jedis = pool.getResource(); 
			boolean ret = jedis.setbit(key, offset, value);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
		 * 获取key对应的
		 * @param key      键
		 * @param offset   二进制偏移位
		 * @return         二进制位的值true标识1，false标识0
		 */
		public boolean getbit(String key, long offset){
			Jedis jedis = pool.getResource(); 
			boolean ret = jedis.getbit(key, offset);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
		 * 统计value中二进制位为1的数量
		 * @param key  键
		 * @return
		 */
		public long bitcount(String key){
			Jedis jedis = pool.getResource(); 
			long ret = jedis.bitcount(key);
			pool.returnResource(jedis);
			return ret;
		}
		
		
		/**
		 * 将字符串添加到指定集合中
		 * @param setName    集合名称
		 * @param members    成员字符串
		 * @return           添加成功大于0，否则等于0
		 */
		public long sadd(String setName,int seconds,String... members){
			Jedis jedis = pool.getResource(); 
			long ret = jedis.sadd(setName,members);
			jedis.expire(setName, seconds);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
		 * 返回集合的成员数，集合不存在返回0
		 * @param setName    集合名称
		 * @return
		 */
		public long scard(String setName){
			Jedis jedis = pool.getResource(); 
			long ret = jedis.scard(setName);
			pool.returnResource(jedis);
			return ret;
		}
		
		/**
		 * 判断member是否是集合setName中的成员
		 * @param setName    集合名称
		 * @return
		 */
		public boolean sismember(String setName,String member){
			Jedis jedis = pool.getResource(); 
			boolean ret = jedis.sismember(setName, member);
			pool.returnResource(jedis);
			return ret;
		}

		/**
		 * 将字符串添加到列表的表尾
		 * @param listName    列表名称
		 * @param value
		 * @return
		 */
		public long rpush(String listName, String value) {
			Jedis jedis = pool.getResource();
			long ret = jedis.rpush(listName, value);
			pool.returnResource(jedis);
			return ret;
		}

		/**
		 * 移除并返回列表的第一个元素
		 * @param listName   列表名称
	 	 * @return
	 	 */
		public String lpop(String listName) {
			Jedis jedis = pool.getResource();
			String ret = jedis.lpop(listName);
			pool.returnResource(jedis);
			return ret;
		}

		/**
	 	* 获取列表长度
	 	* @param listName
	 	* @return
	 	*/
		public long llen(String listName) {
			Jedis jedis = pool.getResource();
			long ret = jedis.llen(listName);
			pool.returnResource(jedis);
			return ret;
		}

	/**
	 * 单位加一
	 * @param key
	 * @return
	 */
		public long redisIncr(String key) {
			Jedis jedis = pool.getResource();
			long value = jedis.incr(key);
			pool.returnResource(jedis);
			return value;
		}

	/**
	 * 单位减一
	 * @param key
	 * @return
	 */
	public long redisDecr(String key) {
			Jedis jedis = pool.getResource();
			long value = jedis.decr(key);
			pool.returnResource(jedis);
			return value;
		}
		
		public void main1(String args[])throws Exception{
			Jedis jedis = pool.getResource(); 
			System.out.println(jedis.sadd("Exception","1"));
			System.out.println(jedis.scard("Exception"));
			System.out.println(jedis.scard("h"));
//			System.out.println(jedis.bitcount("123456"));
//			System.out.println(jedis.get("123456"));
//			System.out.println(jedis.setbit("123456".getBytes("utf-8"),1,true));
////			System.out.println(jedis.get("123456".getBytes("utf-8")).length);
//			System.out.println(jedis.bitcount("123456".getBytes("utf-8")));
////			System.out.println(jedis.del("123456".getBytes("utf-8")));
//			System.out.println(jedis.exists("123456".getBytes("utf-8")));
//			System.out.println(jedis.expire("123456".getBytes("utf-8"), 1));
//			Thread.sleep(3000);
//			System.out.println(jedis.exists("123456".getBytes("utf-8")));
//			System.out.println(jedis.bitcount("123456".getBytes("utf-8")));
			
//			System.out.println(jedis.getbit("余聪1", 10));
//			System.out.println(jedis.sadd(key, members)exists("余聪1"));
//			Thread.sleep(3000);
//			System.out.println(jedis.del("余聪"));
//			System.out.println(getbit("test",10));
//			System.out.println(setbit("test",Integer.MAX_VALUE,false));
//			System.out.println(jedis.bitcount("test"));
//			System.out.println(getbit("hah",10));
			
//			System.out.println(redisGet("test"));
			pool.returnResource(jedis);
		}
//		
		 public String getLocalHostIP() {
	          String ip; 
	          try { 
	               /**返回本地主机。*/ 
	               InetAddress addr = InetAddress.getLocalHost(); 
	               /**返回 IP 地址字符串（以文本表现形式）*/ 
	               ip = addr.getHostAddress();  
	          } catch(Exception ex) { 
	              ip = ""; 
	          } 
	            
	          return ip; 
	     } 
}
