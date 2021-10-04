/**
 * A HashTable for a graph that returns the number of the specified node object in the graph
 * graphs only see vertices as numbers -> from that number, we must quickly get the node object
 *
 * Node -> Node number in graph
 *
 * @param <N> the type of the node object
 */
public class NodeIDTable<N>{
    static class TableEntry<T>{
        T node;  //key
        int id;  //value
        TableEntry<T> next;

        public TableEntry(T node, int id) {
            this.node = node;
            this.id = id;
        }
    }

    TableEntry<N>[] table;

    NodeIDTable(int size){
        table = new TableEntry[size];
    }

    void add(N node, int id){
        TableEntry<N> n = new TableEntry<>(node, id);
        int poz = n.node.hashCode()%table.length;
        n.next = table[poz];
        table[poz] = n;
    }
    int getId(N node){
        int poz = node.hashCode()%table.length;
        TableEntry<N> t = table[poz];

        while(t!=null){
            if(t.node.equals(node)) return t.id;
            t=t.next;
        }
        throw new RuntimeException("No such node in graph");

    }
}
