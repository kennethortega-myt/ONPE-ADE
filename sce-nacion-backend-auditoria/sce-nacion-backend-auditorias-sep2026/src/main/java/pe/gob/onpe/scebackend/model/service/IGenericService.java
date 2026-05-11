package pe.gob.onpe.scebackend.model.service;


import java.util.List;

public interface IGenericService<T,E> {

    void save(T t);

    List<E> listAll();

    void updateStatus(Integer status,Integer id);
}
