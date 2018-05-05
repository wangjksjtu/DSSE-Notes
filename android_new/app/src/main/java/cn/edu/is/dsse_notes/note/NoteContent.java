package cn.edu.is.dsse_notes.note;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for providing sample title for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class NoteContent {

    /**
     * An array of sample (note) items.
     */
    public static List<NoteItem> ITEMS = new ArrayList<NoteItem>();

    /**
     * A map of sample (note) items, by ID.
     */
    public static Map<String, NoteItem> ITEM_MAP = new HashMap<String, NoteItem>();

    /**
     * A tag list
     */
    public static String[] tagList = {"Miscellaneous", "Errand", "Study", "Work", "Research",
            "High", "Medium", "Low", "Evening", "Afternoon", "Morning", "Library", "Classroom",
            "Home", "10h", "9h", "8h", "7h", "6h", "5h", "4h", "3h", "2h", "1h",
            "30min", "Easy", "Challenge"};

    public static final String getNewItemID() {
        return String.valueOf(ITEMS.size());
    }

    private static String dummyRemoteKey = null;

    public static String getDummyRemoteKey() {
        if (dummyRemoteKey == null) {
            dummyRemoteKey = new String(new char[100]).replace("\0", "0");
        }
        return dummyRemoteKey;
    }

    private static void addItem(NoteItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static NoteItem createDummyItem(int position) {
        return new NoteItem(String.valueOf(position), "Item " + position, makeDetails(position));
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
     * A note item representing a piece of title.
     */
    public static class NoteItem implements Parcelable{
        public String id;
        public String title;
        public String details;
        public String remoteId = null;
        public ArrayList<Integer> tags = new ArrayList<>();

        public NoteItem(String id, String title, String details) {
            this.id = id;
            this.title = title;
            this.details = details;
        }

        public String getKeyString() {
            Set<Integer> keySet = new HashSet<>(tags);
            StringBuilder remoteKeyBuilder = new StringBuilder();
            for (int i = 0; i < 100; ++i) {
                if (keySet.contains(i)) {
                    remoteKeyBuilder.append("1");
                } else {
                    remoteKeyBuilder.append("0");
                }
            }
            return remoteKeyBuilder.toString();
        }

        @Override
        public String toString() {
            return title;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(id);
            parcel.writeString(title);
            parcel.writeString(details);
        }

        public static final Parcelable.Creator<NoteItem> CREATOR = new Parcelable.Creator<NoteItem>() {
            @Override
            public NoteItem createFromParcel(Parcel source) {
                return new NoteItem(source.readString(), source.readString(), source.readString());
            }

            @Override
            public NoteItem[] newArray(int size) {
                return new NoteItem[size];
            }
        };
    }
}
