package io.yapix.process.yapi.process;

import com.google.common.base.Strings;
import io.yapix.base.sdk.yapi.YapiClient;
import io.yapix.base.sdk.yapi.YapiException;
import io.yapix.base.sdk.yapi.model.InterfaceVo;
import io.yapix.base.sdk.yapi.model.YapiCategory;
import io.yapix.base.sdk.yapi.model.YapiCategoryAddRequest;
import io.yapix.base.sdk.yapi.model.YapiInterface;
import io.yapix.base.sdk.yapi.model.YapiListInterfaceResponse;
import io.yapix.base.util.NotificationUtils;
import io.yapix.model.Api;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Yapi上传
 */
public class YapiUploader {

    private final YapiClient client;
    private final Map<String, Integer> menuCatIdCache = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger(YapiUploader.class.getName());

    public YapiUploader(YapiClient client) {
        this.client = client;
    }

    public YapiInterface upload(Integer projectId, Api api) {
        YapiInterface data = YapiDataConvector.convert(projectId, api);
        Integer categoryId = getCatIdOrCreate(data.getProjectId(), data.getMenu());
        data.setCatid(String.valueOf(categoryId));
        addOrUpdate(data);
        return data;
    }


    /**
     * 获取或者创建分类
     */
    public Integer getCatIdOrCreate(Integer projectId, String menu) {
        return menuCatIdCache.computeIfAbsent(menu, key -> {
            Integer catId = menuCatIdCache.get(menu);
            if (catId != null) {
                return catId;
            }
            try {
                List<YapiCategory> list = client.getCategories(projectId);
                String[] menus = menu.split("/");
                // 循环多级菜单，判断是否存在，如果不存在就创建
                //  解决多级菜单创建问题
                Integer parent_id = -1;
                Integer now_id = null;
                for (int i = 0; i < menus.length; i++) {
                    if (Strings.isNullOrEmpty(menus[i])) {
                        continue;
                    }
                    boolean needAdd = true;
                    now_id = null;
                    for (YapiCategory yapiCategory : list) {
                        if (yapiCategory.getName().equals(menus[i])) {
                            needAdd = false;
                            now_id = yapiCategory.getId();
                            break;
                        }
                    }
                    if (needAdd) {
                        now_id = this.addCategory(projectId, parent_id, menus[i]);
                    }
                    if (i == (menus.length - 1)) {
                        catId = now_id;
                    } else {
                        parent_id = now_id;
                    }
                }
            } catch (YapiException e) {
                //出现这种情况可能是yapi 版本不支持
            }
            if (catId == null) {
                catId = addCategory(projectId, -1, menu);
            }
            return catId;
        });
    }

    /**
     * 创建或更新接口
     * <p>
     * 判断接口是否存在，不存在直接添加，存在检查接口状态完成不去更新，未完成则更新
     */
    private void addOrUpdate(YapiInterface api) {
        YapiInterface originApi = findOldParamByTitle(api);
        if (originApi != null) {
            api.setId(originApi.getId());
        } else {
            // 新API直接上传
            client.saveInterface(api);
            return;
        }

        // 有差异的才上传
        if (Objects.equals(originApi, api)) {
            NotificationUtils.notifyWarning("API无变更，忽略（部分属性未对比，如果确实有调整，请微调注释以触发变更）：" + api.getPath());
            logger.info("API无变化，不提交");
        } else {
            logger.info("API有差异：\n =>原API：" + originApi + "\n =>新API：" + api);
            client.saveInterface(api);
        }

    }

    public YapiInterface findOldParamByTitle(YapiInterface yapiInterface) {
        YapiListInterfaceResponse interfacesList = client.listInterfaceByCat(yapiInterface.getCatid(), 1, 1000);
        InterfaceVo originInterface = interfacesList.getList().stream()
                .filter(o -> Objects.equals(o.getPath(), yapiInterface.getPath()) && Objects
                        .equals(o.getMethod(), yapiInterface.getMethod()))
                .findFirst().orElse(null);
        if (originInterface == null) {
            originInterface = interfacesList.getList().stream()
                    .filter(o -> o.getTitle().equals(yapiInterface.getTitle()))
                    .findFirst().orElse(null);
        }
        if (originInterface != null) {
            return client.getInterface(originInterface.getId());
        }
        return null;
    }


    /**
     * 创建分类
     */
    private Integer addCategory(Integer projectId, Integer parent_id, String menu) {
        YapiCategoryAddRequest req = new YapiCategoryAddRequest(menu, projectId, parent_id);
        YapiCategory category = client.addCategory(req);
        return category.getId();
    }

}
