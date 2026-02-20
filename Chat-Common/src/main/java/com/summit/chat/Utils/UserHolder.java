package com.summit.chat.Utils;



import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHolder {
    private static final ThreadLocal<String> local = new ThreadLocal<>();
    private static final ThreadLocal<String> roleLocal = new ThreadLocal<>();

    private UserHolder() {
    }

    public static String getUserID() {
        String userID = local.get();
        if (userID == null) {
            log.error("【用户上下文】用户不存在");
            return null;
        }
        return userID;
    }

    public static String getRole() {
        return roleLocal.get();
    }

    public static void setRole(String role) {
        roleLocal.set(role);
    }

    public static void setUserID(String userID) {
        local.set(userID);
    }

    public static void remove(){
        local.remove();
        roleLocal.remove();
    }
}
