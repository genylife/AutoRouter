package router;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * 路由表字符串映射管理类
 */
class RouterServiceManager {


    private static final HashMap<String, String> cache = new HashMap<>();


    static Class find(String name) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        if(cache.size() == 0) {
            Class<?> routerServiceClazz = Class.forName("router.Router");
            Field nameMap = routerServiceClazz.getDeclaredField("NAME_MAP");
            nameMap.setAccessible(true);
            //noinspection unchecked
            HashMap<String, String> temp = (HashMap<String, String>) nameMap.get(null);
            cache.putAll(temp);
        }
        String toActivity = cache.get(name);

        if(toActivity == null) {
            throw new IllegalArgumentException("the name[" + name + "] does not exist!!");
        }

        return Class.forName(cache.get(name));
    }

}
