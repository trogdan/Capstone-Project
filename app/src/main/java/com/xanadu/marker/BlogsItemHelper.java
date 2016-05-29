package com.xanadu.marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BlogsItemHelper {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<BlogsItem> ITEMS = new ArrayList<BlogsItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, BlogsItem> ITEM_MAP = new HashMap<String, BlogsItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createBlogsItem(i));
        }
    }

    private static void addItem(BlogsItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static BlogsItem createBlogsItem(int position) {
        return new BlogsItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class BlogsItem {
        public final String id;
        public final String content;
        public final String details;

        public BlogsItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
