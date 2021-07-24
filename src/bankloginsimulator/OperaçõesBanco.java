/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankloginsimulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Pichau
 */
public class OperaçõesBanco {
    
    private Connection conexao;
    private Statement bank_statement;
    
    //Construtor
    
    public OperaçõesBanco() {
        
    String dbUrl = "jdbc:mysql://localhost/banklogin";
    String user = "root";
    String pass = "";
    
    Connection conexao1 = null;
    Statement stmt = null;
    
    // Construtor da Conexao
    try {
            
    conexao1 = DriverManager.getConnection(dbUrl,user,pass);
    this.conexao = conexao1;
            
        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
            
        }
        
        // Construtor do Statement que vai executar as querys
        try {
            
        stmt = this.conexao.createStatement();
        this.bank_statement = stmt;
            
        } catch (SQLException e) {
           JOptionPane.showMessageDialog(null, e);
        }
        
    }

    
        
    
    
    public void CadastrarConta(String nome , String cpf , String senha, String tipodeconta) {
        
        ResultSet resultado;
        ResultSet resultado2;
        boolean vazio = false;
        
        // Testa se o cpf colocado não existe na Database
        try {
            
        resultado = this.getBank_statement().executeQuery("select * from dados where cpf like '" + cpf + "'");
        
        vazio = resultado.next() != true;

        
        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
            
        }
        
        try {
            
        if (vazio) {
            
            this.bank_statement.executeUpdate("insert into dados values (default,'" + nome + "','" + cpf + "','" + senha + "','" + tipodeconta + "',default)");
            
            resultado2 = this.getBank_statement().executeQuery("select numconta from dados where cpf like '" + cpf + "'");
            resultado2.next();
            int numeroconta = resultado2.getInt(1);
            JOptionPane.showMessageDialog(null,"Sucesso! O numero de sua conta é: 000" + numeroconta + "!");
            
        } else {
            
            JOptionPane.showMessageDialog(null,"Erro! CPF já cadastrado!");
            
        }
        
        } 
        catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    public boolean Login(String usuario, String senha) {
        
        ResultSet resultado;
        boolean entrou = false;
        String numconta;
        String nome;
        String cpf;
        float saldo;
        
        
        try {
        
        //Procura na DB se existe o cpf do usuario
        resultado = this.getBank_statement().executeQuery("select cpf, senha from dados where cpf like '" + usuario + "'");
            
            //Verifica se o resultado voltou com informações
            if (resultado.next() != false) {
                
                //resultado.next();
                //System.out.println(resultado.getString(2));
                
                //Verifica se a senha informada foi a mesma da cadastrada, se sim abre a conta!
                
                if (senha.equals(resultado.getString(2))) {
                    
                    resultado = null;                    
                    resultado = this.getBank_statement().executeQuery("select numconta,nome,cpf,saldo from dados where cpf like '" + usuario + "'");
                    resultado.next();
                    
                    numconta = resultado.getString(1);
                    nome = resultado.getString(2);
                    cpf = usuario;
                    saldo = resultado.getFloat(4);
                    
                    
                    entrou = true;
                    
                    new Conta(numconta,nome,cpf,saldo).setVisible(true);
                    
                    
                    
                } else {
                    
             JOptionPane.showMessageDialog(null,"Senha incorreta!");   
                    
                    
                }
                
                
                
            } else {
                
             JOptionPane.showMessageDialog(null,"Erro! Conta não existe!");   
                
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        
        return entrou;
        
    }
    
    public String[] LembrarSenha(String usuario, String cpf , String tipodeconta) {
        
        String[] retorno = new String[2];
        ResultSet resultado;
        
        try {
            
        resultado = this.bank_statement.executeQuery("select numconta,nome,cpf,senha,tipoconta from dados where cpf = '" + cpf + "'");
        
            if (resultado.next() != false) {
                
                if ( resultado.getString(2).equals(usuario) && resultado.getString(3).equals(cpf) && resultado.getString(5).equals(tipodeconta) ) {
                    
                    retorno[0] = resultado.getString(1);
                    retorno[1] = resultado.getString(4);
                    
                    
                    
                } else {
                    
                    JOptionPane.showMessageDialog(null, "Usuario ou tipo de conta incorreto na base de dados!");
                    
                    retorno[0] = "???";
                    retorno[1] = "???";
                }
                
                
                
            } else {
                
                JOptionPane.showMessageDialog(null, "Erro! CPF não encotrado");
                retorno[0] = "???";
                retorno[1] = "???";
                
            }
        
        
            
            
        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
            retorno[0] = "???";
            retorno[1] = "???";
            
        }
        
        
        
        
        return retorno;
    }
    
    public float DepositarSacar(String opcao, String cpf, float saldo) {
        
        float opcao_atualizada = 0;
        float valor_opcao;
        
        
        try {
            
            if (opcao.equals("Depositar")) {
                
                valor_opcao = Float.parseFloat(JOptionPane.showInputDialog(null, "Digite o valor que deseja depositar: R$"));
                
                this.bank_statement.executeUpdate("update dados set saldo = saldo + '" + valor_opcao + "' where cpf = '" + cpf + "'");
                
                ResultSet depositar = this.bank_statement.executeQuery("select saldo from dados where cpf = '" + cpf + "'");
                
                depositar.next();
                
                opcao_atualizada = depositar.getFloat(1);
                            
                
                
                
                
                
            } else {
                
                ResultSet sacar = this.bank_statement.executeQuery("select saldo from dados where cpf = '" + cpf + "'");
                sacar.next();
                float temsaldo = sacar.getFloat(1);
                System.out.println("Retorno Database = " + temsaldo);
                
                valor_opcao = Float.parseFloat(JOptionPane.showInputDialog(null, "Digite o valor que deseja sacar: R$"));
                System.out.println("Valor colocado = " + valor_opcao);
                
                               
                if ( temsaldo - valor_opcao >= -0.01 ) {
                    
                    if (temsaldo - valor_opcao == 0) {
                        
                    this.bank_statement.executeUpdate("update dados set saldo = 0 where cpf = '" + cpf + "'");
                    
                    } else {
                        
                    this.bank_statement.executeUpdate("update dados set saldo = saldo - '" + valor_opcao + "' where cpf = '" + cpf + "'");
                    
                    }
                    
                    sacar = null;
                    //System.out.println("Retorno após sacar pós null = " + sacar.getFloat(1));
                    sacar = this.bank_statement.executeQuery("select saldo from dados where cpf = '" + cpf + "'");
                    sacar.next();
                    System.out.println("Retorno após sacar 2 = " + sacar.getFloat(1));
                    opcao_atualizada = sacar.getFloat(1);
                    
                    
                } else {
                    
                    JOptionPane.showMessageDialog(null, "Erro, você está tentando sacar um valor superior ao que você tem na conta!");
                    opcao_atualizada = -1;
                }
                
                
                
            }
            
        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
        }
        
        return opcao_atualizada;
    }
        
    //Getter and Setter

    public Connection getConexao() {
        return conexao;
    }

    public void setConexao(Connection conexao) {
        this.conexao = conexao;
    }

    public Statement getBank_statement() {
        return bank_statement;
    }

    public void setBank_statement(Statement bank_statement) {
        this.bank_statement = bank_statement;
    }
    
    
    
}
