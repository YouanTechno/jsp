/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilisateurs.Server;
import utilisateurs.modeles.User;

/**
 *
 * @author michel
 */

// En Java EE 6 on peut presque se passer du fichier web.xml, les annotations de codes
// sont très pratiques !
@WebServlet(name = "ServletUsers",
     urlPatterns = {"/ServletUsers"},
     initParams = {
         @WebInitParam(name = "ressourceDir", value = "E:\\java2ee\\jsp")
     }
)
public class ServletUsers extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        // appel à init obligatoire sinon cet init sera appelé à chaque fois au lieu d'une seule !
        super.init(config);
        System.out.println("Dans le init ! Appelé une seule fois lors de la première invocation");

        // On charge la base de données des utilisateurs et on initialise dans la classe Server
        // le gestionnaire d'utilisateurs'
        Server.init(config.getInitParameter("ressourceDir"));
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pratique pour décider de l'action à faire
        String action = request.getParameter("action");
        String forwardTo = "";
        String message = "";

        if (action != null) {
            if (action.equals("listerLesUtilisateurs")) {
                Collection<User> liste = Server.uh.getUsers();
                request.setAttribute("listeDesUsers", liste);
                forwardTo = "index.jsp?action=listerLesUtilisateurs";
                message = "Liste des utilisateurs";
            }else if(action.equals("creerUtilisateursDeTest")){
                creeUtilisateurDetest();
                Collection<User> liste = Server.uh.getUsers();
                request.setAttribute("listeDesUsers", liste);
                forwardTo = "index.jsp?action=listerLesUtilisateurs";
                message = "les 3 utilisateurs ont été créés !";
            }
            else if (action.equals("creerUnUtilisateur")) {
                creerUtilisateur(request);
                Collection<User> liste = Server.uh.getUsers();
                request.setAttribute("listeDesUsers", liste);
                forwardTo = "index.jsp?action=listerLesUtilisateurs";
                message = "Tout est bien !";
            }
            else if (action.equals("chercherParLogin")) {
                User user = rechercherUtilisateur(request);
                ArrayList<User> liste = new ArrayList<User>();
                liste.add(user);
                request.setAttribute("listeDesUsers", liste);
                forwardTo = "index.jsp?action=listerLesUtilisateurs";
                message = "tout est bien";
            }
            else if(action.equals("updateUtilisateur")){
                modifierUser(request);
                Collection<User> liste = Server.uh.getUsers();
                request.setAttribute("listeDesUsers", liste);
                forwardTo = "index.jsp?action=listerLesUtilisateurs";
                message = "ça marche!";
                 }
            else if (action.equals("supprimerUtilisateur")) {
                SupprimerUtilisateur(request);
                Collection<User> liste = Server.uh.getUsers();
                request.setAttribute("listeDesUsers", liste);
                forwardTo = "index.jsp?action=listerLesUtilisateurs";
                message = "ça marche !";
            }
            else {
                forwardTo = "index.jsp?action=todo";
                message = "La fonctionnalité pour le paramètre " + action + " est à implémenter !";
            }
        }

        RequestDispatcher dp = request.getRequestDispatcher(forwardTo + "&message=" + message);
        dp.forward(request, response);
        // Après un forward, plus rien ne peut être exécuté après !

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

private void creeUtilisateurDetest(){
        try {
            User user1 = new User("Youan123","Youan", "Toussaint");
            User user2 = new User("Kone123","Hamed", "Kone");
            User user3 = new User("Keit123","Keita", "Keita Mamady");
            Server.uh.addUser(user1);
            Server.uh.addUser(user2);
            Server.uh.addUser(user3);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ServletUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
}

public void creerUtilisateur(HttpServletRequest request) {
        String login, nom, prenom;
        nom = request.getParameter("nom");
        prenom = request.getParameter("prenom");
        login = request.getParameter("login");
        try {
            User user = new User(login, nom, prenom);
            Server.uh.addUser(user);
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ServletUsers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
public User rechercherUtilisateur(HttpServletRequest request) {
        String login;
        login = request.getParameter("login");

        return Server.uh.getUserFromLogin(login);

    }
public boolean SupprimerUtilisateur(HttpServletRequest request) {
        String login;
        login = request.getParameter("login");

        return Server.uh.removeUserFromLogin(login);

    }
public void modifierUser(HttpServletRequest request) throws UnsupportedEncodingException{
        //recupération des informations du user 
          String login, nom, prenom;
          nom = request.getParameter("nom");
          prenom = request.getParameter("prenom");
          login = request.getParameter("login");
    //obtenir un user par son login
        try{
            User user = new User(login, nom, prenom);       
            Server.uh.updateUser(user);     

        }
            catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ServletUsers.class.getName()).log(Level.SEVERE, null, ex);
           }
        
    }
}
