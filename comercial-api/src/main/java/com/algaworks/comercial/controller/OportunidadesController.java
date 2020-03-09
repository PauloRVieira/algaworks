package com.algaworks.comercial.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.algaworks.comercial.model.Oportunidade;
import com.algaworks.comercial.repository.OportunidadeRepository;

@CrossOrigin
@RestController
@RequestMapping("/oportunidades")
public class OportunidadesController {

	@Autowired
	private OportunidadeRepository oportunidades;

	@GetMapping
	public List<Oportunidade> listar() {
		return oportunidades.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Oportunidade> buscar(@PathVariable Long id) {
		Optional<Oportunidade> oportunidade = oportunidades.findById(id);

		if (!oportunidade.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(oportunidade.get());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Oportunidade adicionar(@Valid @RequestBody Oportunidade oportunidade) {
		Optional<Oportunidade> oportunidadeExistentes = oportunidades
				.findByDescricaoAndNomeProspecto(oportunidade.getDescricao(), oportunidade.getNomeProspecto());

		if (oportunidadeExistentes.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Já existe uma oportunidade para este prospecto com a mesma descrição");
		}

		return oportunidades.save(oportunidade);
	}

	@GetMapping("/excluir/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void excluir(@PathVariable Long id) {
		Optional<Oportunidade> oportunidadeFounded = oportunidades.findById(id);
		
		if (!oportunidadeFounded.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Não existe uma oportunidade para este ID");
		}
		
		oportunidades.deleteById(id);
	}
	
	@PostMapping("/excluir")
	public void excluir(@RequestBody Oportunidade oportunidade) {
		if (oportunidade.getId() == null) {
			oportunidade = oportunidades.findByDescricaoAndNomeProspecto(oportunidade.getDescricao(), oportunidade.getNomeProspecto()).get();
		}
		
		oportunidades.delete(oportunidade);
	}
	
	@PostMapping("/alterar")
	public Oportunidade alterar(@Valid @RequestBody Oportunidade oportunidade) {
		if (oportunidade.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ID do prospecto a ser alterado está nulo");
		}
		
		return oportunidades.save(oportunidade);
	}

}
