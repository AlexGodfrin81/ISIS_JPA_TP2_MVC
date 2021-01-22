package galerie.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import galerie.dao.TableauRepository;
import galerie.dao.ArtisteRepository;
import galerie.entity.Artiste;
import galerie.entity.Tableau;
import galerie.entity.Personne;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author ALEX
 */
@Controller
@RequestMapping(path = "/tableau")
public class TableauController {
    
    @Autowired
    private TableauRepository dao;
    
    @Autowired
    private ArtisteRepository dao1;
    
    /**
     * Affiche toutes les catégories dans la base
     *
     * @param model pour transmettre les informations à la vue
     * @return le nom de la vue à afficher ('afficheTableaux.html')
     */
    @GetMapping(path = "show")
    public String afficheTousLesTableaux(Model model) {
        model.addAttribute("tableaux", dao.findAll());
        return "afficheTableaux";
    }
    
    /**
     * Montre le formulaire permettant d'ajouter une galerie
     *
     * @param personne
     * @param tableau initialisé par Spring, valeurs par défaut à afficher dans le formulaire
     * @return le nom de la vue à afficher ('formulaireTableau.html')
     */
    @GetMapping(path = "add")
    public String montreLeFormulairePourAjout(Model model) {
        model.addAttribute("tableau", new Tableau());
        model.addAttribute("artistes", dao1.findAll());
        return "formulaireTableau";
    }
    
    /**
     * Appelé par 'formulaireTableau.html', méthode POST
     *
     * @param tableau Une galerie initialisée avec les valeurs saisies dans le formulaire
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des tableaux
     */
    @PostMapping(path = "save")
    public String ajouteLeTableauPuisMontreLaListe(Tableau tableau, RedirectAttributes redirectInfo) {
        String message;
        try {
            // cf. https://www.baeldung.com/spring-data-crud-repository-save
            dao.save(tableau);
            // Le code de la catégorie a été initialisé par la BD au moment de l'insertion
            message = "Le tableau '" + tableau.getTitre() + "' a été correctement enregistrée";
        } catch (DataIntegrityViolationException e) {
            // Les noms sont définis comme 'UNIQUE' 
            // En cas de doublon, JPA lève une exception de violation de contrainte d'intégrité
            message = "Erreur : Le tableau '" + tableau.getTitre() + "' existe déjà";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableaux.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // POST-Redirect-GET : on se redirige vers l'affichage de la liste		
    }
    
    @GetMapping(path = "delete")
    public String supprimeUneCategoriePuisMontreLaListe(@RequestParam("id") Tableau tableau, RedirectAttributes redirectInfo) {
        String message = "La galerie '" + tableau.getTitre() + "' a bien été supprimée";
        try {
            dao.delete(tableau); // Ici on peut avoir une erreur (Si il y a des expositions pour ce tableau par exemple)
        } catch (DataIntegrityViolationException e) {
            // violation de contrainte d'intégrité si on essaie de supprimer un tableau qui apparait dans des expositions
            message = "Erreur : Impossible de supprimer le tableau '" + tableau.getTitre() + "', il faut d'abord supprimer ses expositions";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheGalerie.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // on se redirige vers l'affichage de la liste
    }
}
