
package model;
 
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "cliente")
public class Cliente {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
 
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
 
    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;
 
    @Column(name = "email", nullable = false, length = 100)
    private String email;
 
    @Column(name = "telefone", length = 20)
    private String telefone;
 
    @Column(name = "ativo", nullable = false)
    private boolean ativo;
 
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
 
    public Cliente() {
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }
 
    public Cliente(String nome, String cpf, String email, String telefone) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }
 
    public boolean podeComprar() {
        return ativo;
    }
 
    public void inativar() {
        this.ativo = false;
    }
 
    public void ativar() {
        this.ativo = true;
    }
 
    public int getId() {
        return id;
    }
 
    public String getNome() {
        return nome;
    }
 
    public String getCpf() {
        return cpf;
    }
 
    public String getEmail() {
        return email;
    }
 
    public String getTelefone() {
        return telefone;
    }
 
    public boolean isAtivo() {
        return ativo;
    }
 
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public void setNome(String nome) {
        this.nome = nome;
    }
 
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
 
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
 
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
 
    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | CPF: " + cpf + " | "
                + (ativo ? "ATIVO" : "INATIVO");
    }
}
 
