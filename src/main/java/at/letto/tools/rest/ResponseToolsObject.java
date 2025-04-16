package at.letto.tools.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import static at.letto.tools.rest.ResponseTools.loadToken;

public class ResponseToolsObject {

    private Logger logger = LoggerFactory.getLogger(ResponseToolsObject.class);

    private String serviceName;
    private String controllerName;
    public ResponseToolsObject(String serviceName, String controllerName) {
        this.serviceName=serviceName;
        this.controllerName=controllerName;
    }
    
    private DtoAndMsg createServiceError(Exception e) {
        logger.error(Arrays.toString(e.getStackTrace()));
        DtoAndMsg<Object> m = new DtoAndMsg<>(e);
        m.getMsg().setMeldung("Exception in: " + serviceName + "-" + controllerName);
        return m;
    }
    /**
     * Aufruf einer Service-Funktion ohne Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S> ResponseEntity<DtoAndMsg<T>> getResponse(Function<S,T> function, S service) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(function.apply(service)));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit einem Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto   Parameter der Service-Funktion
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @param <D>   Typ des Parameters
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D> ResponseEntity<DtoAndMsg<T>> getResponse(BiFunction<S,D,T> function, S service, D dto) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(function.apply(service, dto)));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit zwei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getResponse(FunctionInterfaces.ThreeParameterFunction<S,D1,D2,T> function, S service, D1 dto1, D2 dto2) {
        try {
            T erg = function.apply(service, dto1, dto2);
            return ResponseEntity.ok(new DtoAndMsg<T>(erg));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    /**
     * Aufruf einer Service-Funktion mit drei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getResponse(FunctionInterfaces.FourParameterFunction<S,D1,D2,D3,T> function, S service, D1 dto1, D2 dto2, D3 dto3) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(function.apply(service, dto1, dto2, dto3)));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    /**
     * Aufruf einer Service-Funktion mit vier Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3,D4> ResponseEntity<DtoAndMsg<T>> getResponse(FunctionInterfaces.FiveParameterFunction<S,D1,D2,D3,D4,T> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(function.apply(service, dto1, dto2, dto3, dto4)));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit fünf Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param dto4  4. Parameter der Service-Funktion
     * @param dto5  5. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @param <D4>
     * @param <D5>
     * @return
     */
    public <T,S,D1,D2,D3,D4,D5> ResponseEntity<DtoAndMsg<T>> getResponse(FunctionInterfaces.SixParameterFunction<S,D1,D2,D3,D4,D5,T> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4, D5 dto5) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(function.apply(service, dto1, dto2, dto3, dto4, dto5)));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m, loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }




    /**
     * Aufruf einer Service-Funktion mit einem Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto   Parameter der Service-Funktion
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @param <D>   Typ des Parameters
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D> ResponseEntity<DtoAndMsg<T>> getErrResponse(BiFunction<S,D,T> function, S service, D dto) {
        try {
            T erg = function.apply(service, dto);
            DtoAndMsg<T> ret = erg instanceof String ?
                    new DtoAndMsg<T>((String)erg, loadToken().getSprache()) :
                    new DtoAndMsg<T>(erg);
            return ResponseEntity.ok(ret);
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }



    /**
     * Aufruf einer Service-Funktion mit einem Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * Wenn der Rückgabetyp ein String ist, wird dieser als Fehlermedung interpretiert,
     * das DtoAndMsg enthält kein Rückgabe-Objket und die Fehlermeldung wird gesetzt.
     * @param function
     * @param service
     * @param dto1
     * @param dto2
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getErrResponse(FunctionInterfaces.ThreeParameterFunction<S,D1,D2,T> function, S service, D1 dto1, D2 dto2) {
        try {
            T erg = function.apply(service, dto1, dto2);
            DtoAndMsg<T> dto = erg instanceof String ?
                    new DtoAndMsg<T>((String)erg, loadToken().getSprache()) :
                    new DtoAndMsg<T>(erg);
            return ResponseEntity.ok(dto);
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit drei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall.
     * Wenn der Rückgabetyp ein String ist, wird dieser als Fehlermedung interpretiert,
     * das DtoAndMsg enthält kein Rückgabe-Objket und die Fehlermeldung wird gesetzt.
     * @param function  Aufzurufende Funktion
     * @param service   Service, das die Funktion bereitstellt
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return  Response-Entity mit MsgDto, das im Fehlerfall (Service-Funktion gibt Text zurück) gesetzt wird
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getErrResponse(
            FunctionInterfaces.FourParameterFunction<S,D1,D2,D3,T> function, S service, D1 dto1, D2 dto2, D3 dto3) {
        try {
            T erg = function.apply(service, dto1, dto2, dto3);
            DtoAndMsg<T> dto = erg instanceof String ?
                    new DtoAndMsg<T>((String)erg, loadToken().getSprache()) :
                    new DtoAndMsg<T>(erg);
            return ResponseEntity.ok(dto);
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit vier Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall.
     * Wenn der Rückgabetyp ein String ist, wird dieser als Fehlermedung interpretiert,
     * das DtoAndMsg enthält kein Rückgabe-Objket und die Fehlermeldung wird gesetzt.
     * @param function  Aufzurufende Funktion
     * @param service   Service, das die Funktion bereitstellt
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param dto4  4. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @param <D4>
     * @return  Response-Entity mit MsgDto, das im Fehlerfall (Service-Funktion gibt Text zurück) gesetzt wird
     */
    public <T,S,D1,D2,D3,D4> ResponseEntity<DtoAndMsg<T>> getErrResponse(
            FunctionInterfaces.FiveParameterFunction<S,D1,D2,D3,D4,T> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4) {
        try {
            T erg = function.apply(service, dto1, dto2, dto3, dto4);
            DtoAndMsg<T> dto = erg instanceof String ?
                    new DtoAndMsg<T>((String)erg, loadToken().getSprache()) :
                    new DtoAndMsg<T>(erg);
            return ResponseEntity.ok(dto);
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit fünf Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall.
     * Wenn der Rückgabetyp ein String ist, wird dieser als Fehlermedung interpretiert,
     * das DtoAndMsg enthält kein Rückgabe-Objket und die Fehlermeldung wird gesetzt.
     * @param function  Aufzurufende Funktion
     * @param service   Service, das die Funktion bereitstellt
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param dto4  4. Parameter der Service-Funktion
     * @param dto5  5. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @param <D4>
     * @param <D5>
     * @return  Response-Entity mit MsgDto, das im Fehlerfall (Service-Funktion gibt Text zurück) gesetzt wird
     */
    public <T,S,D1,D2,D3,D4,D5> ResponseEntity<DtoAndMsg<T>> getErrResponse(
            FunctionInterfaces.SixParameterFunction<S,D1,D2,D3,D4,D5,T> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4, D5 dto5) {
        try {
            T erg = function.apply(service, dto1, dto2, dto3, dto4, dto5);
            DtoAndMsg<T> dto = erg instanceof String ?
                    new DtoAndMsg<T>((String)erg, loadToken().getSprache()) :
                    new DtoAndMsg<T>(erg);
            return ResponseEntity.ok(dto);
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    public <T,S,D1,D2,D3,D4,D5,D6> ResponseEntity<DtoAndMsg<T>> getErrResponse(
            FunctionInterfaces.SevenParameterFunction<S,D1,D2,D3,D4,D5,D6,T> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4, D5 dto5, D6 dto6) {
        try {
            T erg = function.apply(service, dto1, dto2, dto3, dto4, dto5, dto6);
            DtoAndMsg<T> dto = erg instanceof String ?
                    new DtoAndMsg<T>((String)erg, loadToken().getSprache()) :
                    new DtoAndMsg<T>(erg);
            return ResponseEntity.ok(dto);
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }







    /**
     * Aufruf einer Service-Funktion ohne Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S> ResponseEntity<DtoAndMsg<T>> getRespData(Function<S,DtoAndMsg<T>> function, S service) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(DtoAndMsg.get(function.apply(service))));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit einem Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto   Parameter der Service-Funktion
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @param <D>   Typ des Parameters
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D> ResponseEntity<DtoAndMsg<T>> getRespData(BiFunction<S,D,DtoAndMsg<T>> function, S service, D dto) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(DtoAndMsg.get(function.apply(service, dto))));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }

    /**
     * Aufruf einer Service-Funktion mit zwei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getRespData(FunctionInterfaces.ThreeParameterFunction<S,D1,D2,DtoAndMsg<T>> function, S service, D1 dto1, D2 dto2) {
        try {
            T erg = DtoAndMsg.get(function.apply(service, dto1, dto2));
            return ResponseEntity.ok(new DtoAndMsg<T>(erg));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    /**
     * Aufruf einer Service-Funktion mit drei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getRespData(FunctionInterfaces.FourParameterFunction<S,D1,D2,D3,DtoAndMsg<T>> function, S service, D1 dto1, D2 dto2, D3 dto3) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(DtoAndMsg.get(function.apply(service, dto1, dto2, dto3))));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    /**
     * Aufruf einer Service-Funktion mit drei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3,D4> ResponseEntity<DtoAndMsg<T>> getRespData(FunctionInterfaces.FiveParameterFunction<S,D1,D2,D3,D4,DtoAndMsg<T>> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(DtoAndMsg.get(function.apply(service, dto1, dto2, dto3, dto4))));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }







    /**
     * Aufruf einer Service-Funktion ohne Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S> ResponseEntity<DtoAndMsg<T>> getRespErrData(Function<S,DtoAndMsg<T>> function, S service) {
        try {
            return ResponseEntity.ok(new DtoAndMsg<T>(DtoAndMsg.get(function.apply(service))));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));        }
    }

    /**
     * Aufruf einer Service-Funktion mit einem Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto   Parameter der Service-Funktion
     * @param <T>   Rückgabe-Typ der Service-Funktion
     * @param <S>   Typ des bereitgestellten Service
     * @param <D>   Typ des Parameters
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D> ResponseEntity<DtoAndMsg<T>> getRespErrData(BiFunction<S,D,DtoAndMsg<T>> function, S service, D dto) {
        try {
            return ResponseEntity.ok(function.apply(service, dto));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));        }
    }

    /**
     * Aufruf einer Service-Funktion mit zwei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return      Response-Entity-Objekt mit eingepacktem DtoAndMsg-Objekt
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getRespErrData(FunctionInterfaces.ThreeParameterFunction<S,D1,D2,DtoAndMsg<T>> function, S service, D1 dto1, D2 dto2) {
        try {
            return ResponseEntity.ok(function.apply(service, dto1, dto2));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    /**
     * Aufruf einer Service-Funktion mit drei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3> ResponseEntity<DtoAndMsg<T>> getRespErrData(FunctionInterfaces.FourParameterFunction<S,D1,D2,D3,DtoAndMsg<T>> function, S service, D1 dto1, D2 dto2, D3 dto3) {
        try {
            return ResponseEntity.ok(function.apply(service, dto1, dto2, dto3));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


    /**
     * Aufruf einer Service-Funktion mit drei Parameter, try-catch-Stacktrace in DtoAndMsg im Fehlerfall
     * @param function
     * @param service
     * @param dto1  1. Parameter der Service-Funktion
     * @param dto2  2. Parameter der Service-Funktion
     * @param dto3  3. Parameter der Service-Funktion
     * @param <T>
     * @param <S>
     * @param <D1>
     * @param <D2>
     * @param <D3>
     * @return
     */
    public <T,S,D1,D2,D3,D4> ResponseEntity<DtoAndMsg<T>> getRespErrData(FunctionInterfaces.FiveParameterFunction<S,D1,D2,D3,D4,DtoAndMsg<T>> function, S service, D1 dto1, D2 dto2, D3 dto3, D4 dto4) {
        try {
            return ResponseEntity.ok(function.apply(service, dto1, dto2, dto3, dto4));
        } catch (MsgException m) {
            return ResponseEntity.ok(new DtoAndMsg<T>(m.getMessage(), loadToken().getSprache()));
        } catch (RestException r) {
            return ResponseEntity.ok(new DtoAndMsg<T>(null, r.getErrMsg()));
        } catch (Exception e) {
            return ResponseEntity.ok(createServiceError(e));
        }
    }


}
