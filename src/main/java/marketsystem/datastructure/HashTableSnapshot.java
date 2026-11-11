package marketsystem.datastructure;

/**
 * Retrato somente leitura de uma {@link HashTable}: quantos buckets existem, quantas
 * chaves estão guardadas e quais buckets estão ocupados.
 *
 * @param capacity        número total de buckets
 * @param size            número de chaves guardadas
 * @param occupiedBuckets buckets que têm pelo menos uma chave, em ordem de índice
 */
public record HashTableSnapshot<K>(int capacity, int size, DoublyLinkedList<Bucket<K>> occupiedBuckets) {

    public double loadFactor() {
        return (double) size / capacity;
    }

    public int collisionBucketCount() {
        int count = 0;
        for (Bucket<K> bucket : occupiedBuckets) {
            if (bucket.hasCollision()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Um bucket ocupado e as chaves encadeadas nele.
     *
     * @param index posição do bucket na tabela
     * @param keys  chaves do bucket, na ordem do encadeamento
     */
    public record Bucket<K>(int index, DoublyLinkedList<K> keys) {

        public boolean hasCollision() {
            return keys.size() > 1;
        }
    }
}
