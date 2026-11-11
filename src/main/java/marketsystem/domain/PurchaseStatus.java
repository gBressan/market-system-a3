package marketsystem.domain;

/** Situação de compra de um produto da lista. */
public enum PurchaseStatus {

    PENDING("Pendente"),
    PARTIAL("Parcial"),
    PURCHASED("Comprado");

    private final String label;

    PurchaseStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
