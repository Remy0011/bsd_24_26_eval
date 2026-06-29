package edu.ban7.demo_spring_bsd_24_26.controller;

import edu.ban7.demo_spring_bsd_24_26.dao.AppUserDao;
import edu.ban7.demo_spring_bsd_24_26.dao.ProduitDao;
import edu.ban7.demo_spring_bsd_24_26.dao.QuestionDao;
import edu.ban7.demo_spring_bsd_24_26.dao.ReponseDao;
import edu.ban7.demo_spring_bsd_24_26.dao.SessionDao;
import edu.ban7.demo_spring_bsd_24_26.model.AppUser;
import edu.ban7.demo_spring_bsd_24_26.model.Produit;
import edu.ban7.demo_spring_bsd_24_26.model.Question;
import edu.ban7.demo_spring_bsd_24_26.model.Reponse;
import edu.ban7.demo_spring_bsd_24_26.model.Session;
import edu.ban7.demo_spring_bsd_24_26.security.IsAdmin;
import edu.ban7.demo_spring_bsd_24_26.security.IsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionDao sessionDao;
    private final ProduitDao produitDao;
    private final QuestionDao questionDao;
    private final ReponseDao reponseDao;
    private final AppUserDao appUserDao;

    @GetMapping
    @IsUser
    public List<Session> list() {
        return sessionDao.findAll();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<?> create(@RequestBody Session sessionRequest) {
        List<Produit> produits = produitDao.findAll();

        if (produits.size() < 10) {
            return ResponseEntity.badRequest().body("Il faut au moins 10 produits pour creer une session.");
        }

        Collections.shuffle(produits);

        Session session = new Session();
        session.setNom(sessionRequest.getNom());
        session.setNombreJoueur(0);
        session.setJoueurs(new ArrayList<>());

        List<Question> questions = new ArrayList<>();
        for (Produit produit : produits.subList(0, 10)) {
            Question question = new Question();
            question.setSession(session);
            question.setProduit(produit);
            questions.add(question);
        }

        session.setQuestions(questions);
        return new ResponseEntity<>(sessionDao.save(session), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Session> update(@PathVariable int id, @RequestBody Session sessionRequest) {
        Optional<Session> optionalSession = sessionDao.findById(id);

        if (optionalSession.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Session session = optionalSession.get();
        session.setNom(sessionRequest.getNom());
        return new ResponseEntity<>(sessionDao.save(session), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!sessionDao.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        sessionDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/join")
    @IsUser
    public ResponseEntity<?> join(@PathVariable int id) {
        Optional<Session> optionalSession = sessionDao.findById(id);
        Optional<AppUser> optionalUser = getCurrentUser();

        if (optionalSession.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Session session = optionalSession.get();
        AppUser user = optionalUser.get();

        if (session.getJoueurs() == null) {
            session.setJoueurs(new ArrayList<>());
        }

        boolean alreadyJoined = session.getJoueurs().stream()
                .anyMatch(joueur -> joueur.getId().equals(user.getId()));

        if (!alreadyJoined) {
            session.getJoueurs().add(user);
            session.setNombreJoueur(session.getJoueurs().size());
            sessionDao.save(session);
        }

        return new ResponseEntity<>(toSessionDto(session), HttpStatus.OK);
    }

    @GetMapping("/{id}/questions")
    @IsUser
    public ResponseEntity<?> questions(@PathVariable int id) {
        if (!sessionDao.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<QuestionDto> questions = questionDao.findBySessionId(id).stream()
                .map(this::toQuestionDto)
                .toList();

        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @PostMapping("/{id}/reponses")
    @IsUser
    public ResponseEntity<?> respond(@PathVariable int id, @RequestBody List<ReponseRequest> requests) {
        Optional<Session> optionalSession = sessionDao.findById(id);
        Optional<AppUser> optionalUser = getCurrentUser();

        if (optionalSession.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Session session = optionalSession.get();
        AppUser user = optionalUser.get();

        boolean joined = session.getJoueurs() != null && session.getJoueurs().stream()
                .anyMatch(joueur -> joueur.getId().equals(user.getId()));

        if (!joined) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous devez rejoindre la session avant de repondre.");
        }

        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.badRequest().body("Au moins une reponse est obligatoire.");
        }

        Map<Integer, Question> questionsById = new HashMap<>();
        for (Question question : questionDao.findBySessionId(id)) {
            questionsById.put(question.getId(), question);
        }

        for (ReponseRequest request : requests) {
            if (request.questionId() == null || request.prix() == null) {
                return ResponseEntity.badRequest().body("Chaque reponse doit contenir questionId et prix.");
            }

            Question question = questionsById.get(request.questionId());

            if (question == null) {
                return ResponseEntity.badRequest().body("Question invalide pour cette session : " + request.questionId());
            }

            Reponse reponse = reponseDao.findByQuestionIdAndAppUserId(request.questionId(), user.getId())
                    .orElseGet(Reponse::new);

            reponse.setQuestion(question);
            reponse.setAppUser(user);
            reponse.setPrix(request.prix());
            reponseDao.save(reponse);
        }

        return new ResponseEntity<>(scoreForUser(id, user), HttpStatus.OK);
    }

    @GetMapping("/{id}/classement")
    @IsUser
    public ResponseEntity<?> ranking(@PathVariable int id) {
        Optional<Session> optionalSession = sessionDao.findById(id);

        if (optionalSession.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<AppUser> joueurs = optionalSession.get().getJoueurs();

        if (joueurs == null) {
            joueurs = List.of();
        }

        List<RankingDto> ranking = joueurs.stream()
                .map(joueur -> scoreForUser(id, joueur))
                .sorted(Comparator.comparingInt(RankingDto::score).reversed())
                .toList();

        return new ResponseEntity<>(ranking, HttpStatus.OK);
    }

    private Optional<AppUser> getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.empty();
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserDao.findByEmail(email);
    }

    private SessionDto toSessionDto(Session session) {
        return new SessionDto(session.getId(), session.getNom(), session.getNombreJoueur());
    }

    private QuestionDto toQuestionDto(Question question) {
        Produit produit = question.getProduit();
        return new QuestionDto(
                question.getId(),
                new ProduitJeuDto(produit.getId(), produit.getNom(), produit.getUrlImage())
        );
    }

    private RankingDto scoreForUser(Integer sessionId, AppUser user) {
        List<Reponse> reponses = reponseDao.findByQuestionSessionIdAndAppUserId(sessionId, user.getId());

        int ecartTotal = reponses.stream()
                .filter(reponse -> reponse.getPrix() != null && reponse.getQuestion().getProduit().getPrix() != null)
                .mapToInt(reponse -> Math.abs(reponse.getPrix() - reponse.getQuestion().getProduit().getPrix()))
                .sum();

        return new RankingDto(
                user.getId(),
                user.getEmail(),
                reponses.size(),
                ecartTotal,
                -ecartTotal
        );
    }

    public record SessionDto(Integer id, String nom, int nombreJoueur) {
    }

    public record ProduitJeuDto(Integer id, String nom, String urlImage) {
    }

    public record QuestionDto(Integer id, ProduitJeuDto produit) {
    }

    public record ReponseRequest(Integer questionId, Integer prix) {
    }

    public record RankingDto(Integer joueurId, String email, int nombreReponses, int ecartTotal, int score) {
    }
}
