Sistem de Admitere și Cazare Studenți

O platformă web pentru gestionarea întregului ciclu de admitere universitară și alocare în cămine studențești - de la depunerea cererii, contractul de studii, repartizarea în cameră și până la plata cazării.

 Ce probleme rezolvă?

| Problemă | Soluție |
| Dosarele de admitere sunt fizice și greu de gestionat | Studenții completează și depun cereri online; secretariatul poate genera contracte PDF |
| Repartizarea în cămin se face manual | Algoritm automat de alocare în funcție de gen, afecțiuni medicale și locuri disponibile |
| Plățile pentru cazare nu sunt urmărite central | Plata integrată prin gateway (2Checkout) cu chitanțe și istoric per student |
| Cererile către secretariat sunt pierdute sau uitate | Studenții depun cereri digitale; flux de stări: Trimisă → Procesată → Ridicată |
| Nu există un profil centralizat al studentului | Profil unic cu date de identitate, bacalaureat, liceu și studii anterioare | 

**Roluri:** `ROLE_ADMIN`, `ROLE_STUDENT`, `ROLE_SECRETARIAT`, `ROLE_CAMIN`

 Module principale
 Modul Admitere
-  Încărcare documente atașate la cerere (scan CI, diplomă, adeverință medicală)
-  Notificare email automată la schimbarea statusului cererii (admis / respins / confirmare)
-  Listă de așteptare automată - dacă un candidat renunță, următorul din listă preia locul
-  Export liste de admiși în format Excel / CSV
-  Grafice și statistici admitere vizibile pentru secretariat (număr cereri pe specializare, rată de admitere)
-  Posibilitate de retragere a cererii de către student înainte de afișarea rezultatelor
-  Vizualizare progres dosar - studentul vede ce date lipsesc din profil înainte de a depune cererea

 Modul Cămin
-  Preferințe coleg de cameră - studentul poate indica o persoană preferată la depunerea cererii
-  Notificare email automată la repartizarea în cămin (bloc, etaj, cameră, pat)
-  Generare automată contract de cazare PDF după repartizare
-  Cerere de schimb de cameră între doi studenți cu aprobare din partea personalului de cămin
-  Vizualizare plan disponibilitate cămin - câte locuri libere există per bloc și etaj
-  Istoric complet plăți cazare cu posibilitate de descărcare chitanță PDF

 Modul Secretariat
-  Posibilitate de respingere a unei cereri cu motiv completat de secretariat
-  Template-uri configurabile pentru adeverințele PDF (text editabil din interfață)
-  Filtrare și căutare avansată în lista de cereri (după nume, facultate, specializare, dată)
-  Export centralizat al cererilor procesate într-o perioadă selectată

 Modul Utilizatori și Cont
-  Resetare parolă prin email (link unic de resetare)
-  Schimbare parolă din contul propriu
-  Încărcare și actualizare fotografie de profil
-  Istoric acțiuni proprii - studentul vede ce cereri a depus, ce plăți a efectuat, ce documente a primit

 Modul Anunțuri
-  Notificare email la publicarea unui anunț nou relevant pentru rolul studentului
-  Marcare anunț ca citit / necitit per utilizator
-  Anunțuri cu atașamente (fișiere PDF, documente)

 Rapoarte și Statistici (Admin)
-  Raport centralizat admitere pe sesiune: total candidați, admiși, respinși, retrași
-  Raport ocupare cămin: locuri totale, ocupate, libere, per bloc și etaj
-  Raport financiar: total plăți cazare încasate per sesiune
-  Log de audit - cine a creat / modificat / șters înregistrări și când



