package ch.ethz.inf.vs.a4.wdmf_api.network_protocol_data;


import java.util.Hashtable;


public class AckTable {
    public static int INIT_SEQ_NR = -1;

    private Hashtable<String, Hashtable<String, Integer>> hash = new Hashtable<>();
    private String owner;

    public AckTable(String node) {
        this.owner = node;
    }

    public AckTable(String node, Hashtable<String, Hashtable<String, Integer>> h) {
            this.owner = node;
            this.hash = h;
    }

    public String getOwner() {
        return owner;
    }

    public Integer get(String sender, String receiver) {
        if (hash.containsKey(sender) && hash.get(sender).containsKey(receiver)) {
            return hash.get(sender).get(receiver);
        } else {
            return -2; //Value?
        }

    }

    private void insert(String sender, String receiver, Integer value) {

        System.out.println();
        Hashtable<String, Integer> h;
        if (this.hash.containsKey(sender)) {
            h = this.hash.get(sender);
        } else {
            h = new Hashtable<String, Integer>();
            this.hash.put(sender, h);
        }

        h.put(receiver, value);
    }

    public void update(String sender, Integer seqNo) {
        if (sender != owner && hash.containsKey(sender) && hash.get(sender).containsKey(owner))
            insert(sender, owner, seqNo);
    }

    public boolean hasKey(String node) {
        return hash.containsKey(node);
    }

    public void delete(String node) {
        this.hash.remove(node);
        for (String key : this.hash.keySet()) {
            this.hash.get(key).remove(node);
        }
    }

    public void merge(AckTable other) {

        if (hash.isEmpty() && other.hash.isEmpty()) {
            this.insert(owner, owner, INIT_SEQ_NR);
            other.insert(other.owner, other.owner, INIT_SEQ_NR);
            merge(other);
            hash.get(owner).remove(owner);
            hash.get(other.owner).remove(other.owner);
            other.hash.get(other.owner).remove(other.owner);
            return;
        }


        if (other.hash.isEmpty()) {
            other.insert(other.owner, other.owner, INIT_SEQ_NR);
            merge(other);
            this.hash.get(other.owner).remove(other.owner);
            other.hash.get(other.owner).remove(other.owner);
            return;
        }

        if (hash.isEmpty()) {
            insert(owner, owner, INIT_SEQ_NR);
            merge(other);
            hash.get(owner).remove(owner);
            return;

        }

        for (String keyS : other.hash.keySet()) {
            //This doesn't have keyS
            if (!this.hash.containsKey(keyS)) {
                this.hash.put(keyS, (Hashtable<String, Integer>) other.hash.get(keyS).clone());

                for (String n : this.hash.keySet()) {

                    if (!other.hash.containsKey(n)) {
                        this.insert(keyS, n, INIT_SEQ_NR);
                        this.insert(n, keyS, INIT_SEQ_NR);
                    }
                }


            }

            //This does have keyS
            if (this.hash.containsKey(keyS)) {

                for (String keyR : other.hash.get(keyS).keySet()) {
                    //This doesn't have keyR or this copy is smaller
                    if (!this.hash.get(keyS).containsKey(keyR) || this.hash.get(keyS).get(keyR) < other.hash.get(keyS).get(keyR)) {
                        this.insert(keyS, keyR, other.hash.get(keyS).get(keyR));
                    }


                }

            }

        }

    }

    public boolean reachedAll(String sender, Integer seqNo) {
        for (String rec : this.hash.get(sender).keySet()) {
            if (sender != rec && this.hash.get(sender).get(rec) < seqNo) {
                return false;
            }
        }

        return true;

    }

    public String toString() {

        return this.owner + this.hash.toString();

    }

    public int width(){
        return hash.size();
    }

    public int notReached(String sender, int seqNo) {
        int result = 0;
        for (String rec : this.hash.get(sender).keySet()) {
            if (sender != rec && this.hash.get(sender).get(rec) < seqNo) {
                result ++;
            }
        }

        return result;
    }

    public void removeNode(String node){
        for(String other : hash.keySet()){
            hash.get(other).remove(node);
        }
        hash.remove(node);
    }
}


