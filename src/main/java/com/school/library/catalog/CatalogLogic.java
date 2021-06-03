package com.school.library.catalog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.commons.CacheName;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.j2cache.J2CacheKit;
import com.jfnice.model.Book;
import com.jfnice.model.Catalog;
import com.school.library.book.BookService;
import com.school.library.constants.RedisConstants;
import com.school.library.constants.SysConstants;

import java.util.*;
import java.util.stream.Collectors;

public class CatalogLogic {

	@Inject
	private CatalogService service;
	@Inject
	private BookService bookService;

	/**
	 * 删除所有时，前端传入的id值
	 */
	private static final Long deleteAllId = -1L;


	/**
	 * 查询学校分类目录记录
	 * @return
	 */
	public List<Catalog> queryListBySchool() {
		//查询学校记录
		List<Catalog> catalogList = this.service.queryBySchool(CurrentUser.getSchoolCode(), SysConstants.SCHOOL_RECORD_SOURCE);
		//查询被删除过的记录
		Catalog delCatalog = this.service.queryFirstDelBySchool(CurrentUser.getSchoolCode(), SysConstants.SCHOOL_RECORD_SOURCE);
		//学校记录为空，且没有被删除过的记录
		if((null == catalogList || catalogList.isEmpty()) && null == delCatalog){
			//批量插入的记录
			List<Catalog> saveList = new ArrayList<>();
			//查询系统默认记录
			List<Catalog> sysList = this.service.queryBySource(SysConstants.SYS_RECORD_SOURCE);

			//查询系统默认redis值
			String sysKey = RedisConstants.SYS_CATALOG_SETTING_KEY;
			String isSysSetting = J2CacheKit.get(CacheName.DEFAULT_SUB_NAME, sysKey);
			//查询学校redis值
			String key = RedisConstants.CATALOG_SETTING_KEY_PREFIX + CurrentUser.getSchoolCode();
			String isSetting = J2CacheKit.get(CacheName.DEFAULT_SUB_NAME, key);
//			J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, sysKey, "0", RedisConstants.TIME_TO_LIVE_SECONDS);
//			J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, key, "0", RedisConstants.TIME_TO_LIVE_SECONDS);
			try {
				//1.系统默认记录为空，则增加系统默认记录
				if((null == sysList || sysList.isEmpty()) && (StrKit.isBlank(isSysSetting) || "0".equals(isSysSetting) )){
					J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, sysKey, "1", RedisConstants.TIME_TO_LIVE_SECONDS);
					this.generateDefaultCatalog(SysConstants.SYS_RECORD_SOURCE);
				}

				//2.增加学校记录
				if((StrKit.isBlank(isSetting)  || "0".equals(isSetting))){
					J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, key, "1", RedisConstants.TIME_TO_LIVE_SECONDS);
					this.generateDefaultCatalog(SysConstants.SCHOOL_RECORD_SOURCE);
				}

				//再将学校目录查询出来
				catalogList = this.service.queryBySchool(CurrentUser.getSchoolCode(), SysConstants.SCHOOL_RECORD_SOURCE);

			}catch (Exception e){
				J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, sysKey, "0", RedisConstants.TIME_TO_LIVE_SECONDS);
				J2CacheKit.put(CacheName.DEFAULT_SUB_NAME, key, "0", RedisConstants.TIME_TO_LIVE_SECONDS);
				e.printStackTrace();
			}


		}
		return catalogList;
	}


	/**
	 * 组装生成默认目录
	 * @param source
	 */
	private void generateDefaultCatalog(String source){
		//默认分类目录数组：[0]catalogNo、[1]catalogName、[3]sort
		String rootKey = "root";
		Map<String, String[][]> map = new HashMap<String , String[][]>();
		String[][] root = new String[][]{{"AAAA", "中图法","1"}};
		map.put("root", root);

		String[][] root_0 = new String[][]{{"A", "马列主义、毛泽东思想、邓小平理论","1"}, {"B", "哲学、宗教","2"}};
		map.put("root_0", root_0);

		String[][] root_0_0 = new String[][]{{"A0", "马克思、恩格斯著作","1"}, {"A1", "列宁著作","2"}, {"A2", "斯大林著作","3"}};
		map.put("root_0_0", root_0_0);

		String[][] root_0_1 = new String[][]{{"B0", "哲学理论","1"}, {"B1", "世界哲学","2"}, {"B2", "中国哲学","3"}};
		map.put("root_0_1", root_0_1);

		this.insertDefaultCatalog(rootKey, source, 0L, map);

	}

	/**
	 * 插入默认目录
	 * @param rootKey
	 * @param source
	 * @param pid
	 * @param map
	 */
	private void insertDefaultCatalog(String rootKey, String source, Long pid, Map<String, String[][]> map){
		String[][] array = map.get(rootKey);
		for(int i = 0, len = array.length; i < len; i++){
			Catalog c = this.saveCatalog(source, pid, array[i][0], array[i][1], array[i][2]);
			String nextKey = rootKey + "_" + i;
			if(null!= map.get(nextKey)){
				insertDefaultCatalog(nextKey, source, c.getId(), map);
			}
		}

	}

	/**
	 * 保存目录
	 * @param source
	 * @param pid
	 * @param catalogNo
	 * @param catalogName
	 * @param sort
	 * @return
	 */
	private Catalog saveCatalog(String source, Long pid, String catalogNo, String catalogName, String sort){
		Catalog c = new Catalog();
		c.setUpdateTime(new Date());
		//c.setUpdateUserCode(CurrentUser.getUserCode());
		c.setDel(false);
		if(SysConstants.SCHOOL_RECORD_SOURCE.equals(source)){
			c.setSchoolCode(CurrentUser.getSchoolCode());
		}
		//c.setCreateUserName(CurrentUser.getUserName());
		//c.setCreateUserCode(CurrentUser.getUserCode());
		c.setCreateTime(new Date());
		c.setSource(source);
		c.setPid(pid);
		c.setSort(Integer.parseInt(sort));
		c.setCatalogName(catalogName);
		c.setCatalogNo(catalogNo);
		this.service.save(c);
		return c;
	}

	/**
	 * 生成一个数据库要保存的新对象
	 * @param source
	 * @return
	 */
	private Catalog createNewCatalog(String source){
		Catalog c = new Catalog();
		c.setUpdateTime(new Date());
		c.setUpdateUserCode(CurrentUser.getUserCode());
		c.setDel(false);
		c.setSchoolCode(CurrentUser.getSchoolCode());
		c.setCreateUserName(CurrentUser.getUserName());
		c.setCreateUserCode(CurrentUser.getUserCode());
		c.setCreateTime(new Date());
		c.setSource(source);
		return c;
	}

	/**
	 * 前端查询
	 * @return
	 */
	public JSONObject queryIndex(){
		JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		List<Catalog> catalogList = this.queryListBySchool();
		if(null!= catalogList && !catalogList.isEmpty()){
			//1.找出根节点，组装映射关系
			List<Catalog> rootList = new ArrayList<>();
			Map<Long, List<Catalog>> map = new TreeMap<>();
			rootList = catalogList.stream().filter(c -> c.getPid().longValue() == 0L).collect(Collectors.toList());
			catalogList.forEach(c -> {
				if(null!= map.get(c.getPid())){
					map.get(c.getPid()).add(c);
				}else{
					List<Catalog> tempList = new ArrayList<>();
					tempList.add(c);
					map.put(c.getPid(), tempList);
				}
			});
			//2.按照前端tree方式组装成树
			array = CatalogKit.generateTree(rootList, map);
		}
		data.put("list", array);
		return data;
	}

	/**
	 * 保存信息
	 * @param c
	 */
	public void saveInfo(Catalog c){
		String schoolCode = CurrentUser.getSchoolCode();
		Map<String, Object> resultMap = new HashMap<>();
		//判断分类号、分类名称是否重复
		c.setSchoolCode(schoolCode);
		if(!this.service.isUnique(c, "school_code", "catalog_no", "catalog_name")){
			throw new ErrorMsg("分类号、分类名称重复，保存失败");
		}
		//判断自己的父类不能是自己
		if(null != c.getPid() && null != c.getId() && c.getPid().longValue() == c.getId().longValue()){
			throw new ErrorMsg("自己不能是自己的父目录，保存失败");
		}
		//设置默认父id为0（根节点）
		if(null == c.getPid()){
			c.setPid(0L);
		}
		//1.新增
		if(null == c.getId()){
			//查询同节点下的最大排序号，将其排在后面
			Integer maxSort = this.service.queryMaxSortByPid(schoolCode, c.getPid());
			c.setSort(maxSort + 1);
			c.setCreateTime(new Date());
			c.setCreateUserCode(CurrentUser.getUserCode());
			c.setCreateUserName(CurrentUser.getUserName());
			c.setDel(false);
			c.setSchoolCode(schoolCode);
			c.setSource(SysConstants.SCHOOL_RECORD_SOURCE);
			c.setUpdateTime(new Date());
			c.setUpdateUserCode(CurrentUser.getUserCode());
			this.service.save(c);
		}else{
			//2.编辑
			Catalog cDb = this.service.queryById(schoolCode, c.getId());
			if(null == cDb){
				throw new ErrorMsg("保存失败，请刷新页面后重试");
			}
			//判断ids里的id有没有被使用过，被用过则不能删除
			/*Book book = this.bookService.queryByCatalogIds(CurrentUser.getSchoolCode(), c.getId() + "");
			if(null != book){
				throw new ErrorMsg("目录已被使用，不能修改");
			}*/
			c.setSort(cDb.getSort());
			//父节点如果改变了，要修改排序号
			if(c.getPid().longValue() != cDb.getPid().longValue()){
				Integer maxSort = this.service.queryMaxSortByPid(schoolCode, c.getPid());
				c.setSort(null != maxSort ? (maxSort + 1) : 1);
			}
			cDb.remove("pid", "catalog_no", "catalog_name", "sort");
			c._setAttrs(cDb);
			c.setCreateUserName(cDb.getCreateUserName() == null ? CurrentUser.getUserName() : cDb.getCreateUserName());
			c.setCreateUserCode(cDb.getCreateUserCode() == null ? CurrentUser.getUserCode() : cDb.getCreateUserCode());
			c.setUpdateUserCode(CurrentUser.getUserCode());
			c.setUpdateTime(new Date());
			this.service.update(c);
		}

	}

	/**
	 * 逻辑删除记录
	 * @param id
	 */
	public void logicDelete(Long id){

		String ids = "''";
		//删除所有
		if(deleteAllId.longValue() == id.longValue()){
			//查询所有目录id
			List<Catalog> catalogList = this.service.queryBySchool(CurrentUser.getSchoolCode(), SysConstants.SCHOOL_RECORD_SOURCE);
			ids = catalogList.stream().map(c -> c.getId().toString()).collect(Collectors.joining(","));
		}else{
			//查找id的所有子目录
			List<Record> recordList = this.getAllLeafPoint(id + "", CurrentUser.getSchoolCode());
			ids = recordList.stream().map(r -> r.getStr("id")).collect(Collectors.joining(","));
		}

		if(!Strings.isNullOrEmpty(ids)){
			//判断ids里的id有没有被使用过，被用过则不能删除
			Book book = this.bookService.queryByCatalogIds(CurrentUser.getSchoolCode(), ids);
			if(null != book){
				throw new ErrorMsg("目录或者子目录已被使用，不能删除");
			}
			this.service.logicDeleteByIds(CurrentUser.getSchoolCode(), CurrentUser.getUserCode(), new Date(), ids);
		}

	}

	/**
	 * 获取所有叶子节点
	 */
	public List<Record> getAllLeafPoint(String catalogId,String unitCode){
		Kv kv = Kv.by("id", catalogId).set("unitCode",unitCode);
		SqlPara sqlPara = Db.getSqlPara("CatalogLogic.findLeafCatalog", kv);
		return Db.find(sqlPara);

	}

	/**
	 * 查询某个目录（包括其子目录）是否被使用过
	 * @param id
	 * @return
	 */
	public int getUsed(Long id){
		int used = 0;
		if(null == id){
			return used;
		}
		//查找id的所有子目录
		List<Record> recordList = this.getAllLeafPoint(id + "", CurrentUser.getSchoolCode());
		String ids = recordList.stream().map(r -> r.getStr("id")).collect(Collectors.joining(","));
		if(!Strings.isNullOrEmpty(ids)){
			//判断ids里的id有没有被使用过，被用过则不能删除
			Book book = this.bookService.queryByCatalogIds(CurrentUser.getSchoolCode(), ids);
			if(null != book){
				used = 1;
			}
		}
		return used;
	}

}