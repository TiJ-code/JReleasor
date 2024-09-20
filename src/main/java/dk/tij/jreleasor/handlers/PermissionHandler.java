package dk.tij.jreleasor.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class PermissionHandler {

    public static boolean HasPermission(Member member, Permission permission) {
        return member.hasPermission(permission);
    }

}
