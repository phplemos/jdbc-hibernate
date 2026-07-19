
package model;
 
import exception.NegocioException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
@Entity
@Table(name = "venda")
public class Venda {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
 
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
 
    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusVenda status;
 
    @Column(name = "total_bruto", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalBruto;

    @Column(name = "desconto", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double desconto;

    @Column(name = "total_liquido", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalLiquido;
    
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens;
 
    public Venda() {
        this.itens = new ArrayList<>();
        this.status = StatusVenda.ABERTA;
        this.dataVenda = LocalDateTime.now();
    }
 
    public Venda(Cliente cliente) {
        this();
        if (!cliente.podeComprar()) {
            throw new NegocioException("Cliente inativo não pode realizar compras.");
        }
        this.cliente = cliente;
    }
 
    public void adicionarItem(ItemVenda item) {
        if (!status.permiteEdicao()) {
            throw new NegocioException("Venda no status " + status + " não permite edição.");
        }
        item.setVenda(this);
        itens.add(item);
        recalcularTotais();
    }
 
    public void removerItem(int indice) {
        if (!status.permiteEdicao()) {
            throw new NegocioException("Venda no status " + status + " não permite edição.");
        }
        if (indice < 0 || indice >= itens.size()) {
            throw new NegocioException("Índice de item inválido.");
        }
        itens.remove(indice);
        recalcularTotais();
    }
 
    public void aplicarDesconto(double desconto) {
        if (desconto < 0) {
            throw new NegocioException("Desconto não pode ser negativo.");
        }
        if (desconto > totalBruto) {
            throw new NegocioException("Desconto não pode ser maior que o total bruto.");
        }
        this.desconto = desconto;
        recalcularTotais();
    }
 
    public void recalcularTotais() {
        this.totalBruto = itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        this.totalLiquido = Math.max(0, totalBruto - desconto);
    }
 
    public void confirmar() {
        if (itens.isEmpty()) {
            throw new NegocioException("Não é possível confirmar venda sem itens.");
        }
        if (!status.permiteEdicao()) {
            throw new NegocioException("Venda não pode ser confirmada no status " + status);
        }
        this.status = StatusVenda.CONFIRMADA;
    }
 
    public void cancelar() {
        if (!status.permiteCancelamento()) {
            throw new NegocioException("Venda não pode ser cancelada no status " + status);
        }
        this.status = StatusVenda.CANCELADA;
    }
 
    public int getId() {
        return id;
    }
 
    public Cliente getCliente() {
        return cliente;
    }
 
    public LocalDateTime getDataVenda() {
        return dataVenda;
    }
 
    public StatusVenda getStatus() {
        return status;
    }
 
    public double getTotalBruto() {
        return totalBruto;
    }
 
    public double getDesconto() {
        return desconto;
    }
 
    public double getTotalLiquido() {
        return totalLiquido;
    }
 
    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(itens);
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
 
    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }
 
    public void setStatus(StatusVenda status) {
        this.status = status;
    }
 
    public void setTotalBruto(double totalBruto) {
        this.totalBruto = totalBruto;
    }
 
    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }
 
    public void setTotalLiquido(double totalLiquido) {
        this.totalLiquido = totalLiquido;
    }
 
    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Venda #").append(id)
          .append(" | Cliente: ").append(cliente.getNome())
          .append(" | Status: ").append(status)
          .append(" | Data: ").append(dataVenda).append("\n");
        for (ItemVenda item : itens) {
            sb.append("  ").append(item).append("\n");
        }
        sb.append("  Total Bruto: R$ ").append(totalBruto)
          .append(" | Desconto: R$ ").append(desconto)
          .append(" | Total Líquido: R$ ").append(totalLiquido);
        return sb.toString();
    }
}
 
