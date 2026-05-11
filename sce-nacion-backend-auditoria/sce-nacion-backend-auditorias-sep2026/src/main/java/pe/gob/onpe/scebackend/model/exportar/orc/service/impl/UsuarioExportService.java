package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UsuarioExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMigracionMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IUsuarioExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IUsuarioExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.repository.UsuarioRepository;
import pe.gob.onpe.scebackend.model.repository.MigracionRepository;

@Service
public class UsuarioExportService extends MigracionService<UsuarioExportDto, Usuario, String> implements IUsuarioExportService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private IUsuarioExportMapper usuarioMapper;
	
	@Override
	public MigracionRepository<Usuario, String> getRepository() {
		return usuarioRepository;
	}

	@Override
	public IMigracionMapper<UsuarioExportDto, Usuario> getMapper() {
		return usuarioMapper;
	}

}
