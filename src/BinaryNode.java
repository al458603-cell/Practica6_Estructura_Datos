import java.util.Objects;

public class BinaryNode<E> {
        private E data;
        private BinaryNode<E> left;
        private BinaryNode<E> right;

        public BinaryNode(E data) {
            this(data, null, null);
        }

        public BinaryNode(E data, BinaryNode<E> left, BinaryNode<E> right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        public E getData() { return data; }
        public BinaryNode<E> left() { return left; }
        public BinaryNode<E> right() { return right; }

        public void setData(E data) { this.data = data; }
        public void setLeft(BinaryNode<E> left) { this.left = left; }
        public void setRight(BinaryNode<E> right) { this.right = right; }

        public boolean hasLeft() { return left != null; }
        public boolean hasRight() { return right != null; }
        public boolean isLeaf() { return !hasLeft() && !hasRight(); }

        // Hoja: "dato". Nodo interno: "dato(izquierdo, derecho)". Un hijo ausente se muestra como "-".
        @Override
        public String toString() {
            if (isLeaf()) {
                return String.valueOf(data);
            }
            return data + "(" + (hasLeft() ? left : "-") + ", " + (hasRight() ? right : "-") + ")";
        }

        // Dos nodos son iguales si sus subárboles completos tienen la misma forma y los mismos datos
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BinaryNode)) return false;
            BinaryNode<?> other = (BinaryNode<?>) o;
            return Objects.equals(data, other.data)
                    && Objects.equals(left, other.left)
                    && Objects.equals(right, other.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, left, right);
        }

}
