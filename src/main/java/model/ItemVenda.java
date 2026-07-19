
package model;
 
import exception.NegocioException;
import jakarta.persistence.*;
 
@Entity
@Table(name = "item_venda")
public class ItemVenda {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
 
    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;
 
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;
 
    @Column(name = "quantidade", nullable = false)
    private int quantidade;
 
    @Column(name = "preco_unitario", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double precoUnitario;

    @Column(name = "subtotal", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double subtotal;
 
    public ItemVenda() {
    }
 
    public ItemVenda(Produto produto, int quantidade) {
        if (!produto.podeSerVendido()) {
            throw new NegocioException("Produto inativo não pode ser vendido.");
        }
        if (quantidade <= 0) {
            throw new NegocioException("Quantidade do item deve ser maior que zero.");
        }
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPrecoVenda();
        calcularSubtotal();
    }
 
    public void calcularSubtotal() {
        this.subtotal = quantidade * precoUnitario;
    }
 
    public int getId() {
        return id;
    }
 
    public Venda getVenda() {
        return venda;
    }
 
    public Produto getProduto() {
        return produto;
    }
 
    public int getQuantidade() {
        return quantidade;
    }
 
    public double getPrecoUnitario() {
        return precoUnitario;
    }
 
    public double getSubtotal() {
        return subtotal;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public void setVenda(Venda venda) {
        this.venda = venda;
    }
 
    public void setProduto(Produto produto) {
        this.produto = produto;
    }
 
    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new NegocioException("Quantidade do item deve ser maior que zero.");
        }
        this.quantidade = quantidade;
        calcularSubtotal();
    }
 
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
        calcularSubtotal();
    }
 
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
 
    @Override
    public String toString() {
        return produto.getNome() + " | Qtd: " + quantidade
                + " | Unit: R$ " + precoUnitario
                + " | Subtotal: R$ " + subtotal;
    }
}
 
