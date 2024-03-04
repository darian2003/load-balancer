BALAGIU DARIAN

# Scopul si cerinta proiectului sunt descrise in fisierul "Load Balancer.pdf"

Cerinta:
Scopul acestei teme este implementarea unui sistem de planificare a task-urilor intr-un datacenter, folosind
Java Threads. Sistemul va folosi diverse politici de planificare precum Round Robin, Shortest Queue, Size INterval Task Assignemnt, Least Work Left.

MyDispatcher.java
    In aceasta clasa am definit metoda addTask(Task task) mostenita din clasa parinte. Pentru sincronizarea
generatoarelor de task-uri care lucreaza in paralel si pot apela simultan metoda addTask a dispatcher-ului (lucru care ar fi dus la forwarding-ul gresit al task-urilor catre noduri),
am definit metoda folosind cuvantul cheie "synchronized", care face imposibil ca mai multe thread-uri sa acceseze zona critica in acelasi timp.
    Cei 4 algoritmi de repartizare a task-urilor sunt implementati precum a fost specificat in enunt, dar am adaugat comentarii in cod pentru claritate.

MyHost.java
    Problema pe care trebuie sa o rezolvam in cadrul acestei clase este urmatoarea:
Metoda run() (pe care thread-ul corespunzator Host-ului o ruleaza ciclic pana la primirea semnalului de shutdown)
si metoda addTask(Task task) din MyHost (care adauga task-uri in coada de asteptare si este apelata de catre thread-ul Dispatcher-ului)
pot accesa & modifica coada de asteptare in acelasi timp, determinand aparitia unor race conditions.
    Solutia aleasa de mine la aceasta problema este crearea cozii de asteptare pe baza tipului de date prezentat in
laboratorul 6: BlockingQueue, iar pentru usurinta adaugarii task-urilor in coada pe baza prioritatii lor,
am extins implementarea la PriorityBlockingQueue.
    Am creat propriul comparator prin clasa MyTaskComparator definita la finalul fisierului.
    Metoda run() ruleaza continuu, pana la primirea semnalului de shutdown() de la thread-ul principal ce ruleaza Main.
Deoarece atat run() (prin thread-ul rulat de Host), cat si shutdown() (print thread-ul rulat de Main) acceseaza si modifica variabila booleaana running,
aceasta a fost creata ca si variabila atomica.
    In metoda run(), task-ul curent (currentTask) va fi rulat pe thread pentru o cuanta de timp de minim 200ms, dupa care se vor face verificarile de finalizare
si de preemptare ale task-ului, care pot duce la modificarile task-ului curent si a cozii de asteptare.
Acest ciclu continua pana cand toata munca a fost terminata sau thread-ul primeste semnalul de shutdown().
