package src;

/**
 * Created by arnavkansal on 10/04/16.
 */
public class utility {
    public static class pair{
        public int left;
        public int right;

        public static pair make_pair(int left, int right){
            pair ret = new pair();
            ret.left = left;
            ret.right = right;
            return ret;
        }
    }
}
