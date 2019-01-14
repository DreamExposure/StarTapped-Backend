package org.dreamexposure.tap.core.utils;

import org.dreamexposure.tap.core.objects.post.IPost;

import java.util.List;
import java.util.UUID;

public class PostUtils {
    public static boolean doesNotHavePost(List<IPost> posts, UUID id) {
        for (IPost p : posts) {
            if (id.equals(p.getId()))
                return false;
        }
        return true;
    }
}
