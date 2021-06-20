package com.vicentecabrera.springboot.app.controllers;


import com.vicentecabrera.springboot.app.models.entity.Cliente;
import com.vicentecabrera.springboot.app.models.service.IClienteService;
import com.vicentecabrera.springboot.app.models.service.IUploadFileService;
import com.vicentecabrera.springboot.app.util.paginator.PageRender;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes({"cliente"})
public class ClienteController {
    @Autowired
    private IClienteService clienteService;
    @Autowired
    private IUploadFileService uploadFileService;

    public ClienteController() {
    }

    @GetMapping({"/uploads/{filename:.+}"})
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
        Resource recurso = null;

        try {
            recurso = this.uploadFileService.load(filename);
        } catch (MalformedURLException var4) {
            var4.printStackTrace();
        }

        return ((BodyBuilder)ResponseEntity.ok().header("Content-Disposition", new String[]{"attachment; filename=\"" + recurso.getFilename() + "\""})).body(recurso);
    }

    @GetMapping({"/ver/{id}"})
    public String ver(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        Cliente cliente = clienteService.findOne(id);
        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en DDBB");
            return "redirect:/listar";
        } else {
            model.put("cliente", cliente);
            model.put("titulo", "Detalle cliente: " + cliente.getNombre());
            return "ver";
        }
    }

    @RequestMapping(
        value = {"/listar"},
        method = {RequestMethod.GET}
    )
    public String listar(@RequestParam(name = "page",defaultValue = "0") int page, Model model) {
        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Cliente> clientes = clienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }

    @RequestMapping({"/form"})
    public String crear(Map<String, Object> model) {
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", "Formulario de Cliente");
        return "form";
    }

    @RequestMapping({"/form/{id}"})
    public String editar(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        Cliente cliente = null;
        if (id > 0L) {
            cliente = clienteService.findOne(id);
            if (cliente == null) {
                flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD!");
                return "redirect:/listar";
            } else {
                model.put("cliente", cliente);
                model.put("titulo", "Editar Cliente");
                return "form";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
            return "redirect:/listar";
        }
    }

    @RequestMapping(
        value = {"/form"},
        method = {RequestMethod.POST}
    )
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de Cliente");
            return "form";
        } else {
            String uniqueFilename;
            if (!foto.isEmpty()) {
                if (cliente.getId() != null && cliente.getId() > 0L && cliente.getFoto() != null && cliente.getFoto().length() > 0) {
                    uploadFileService.delete(cliente.getFoto());
                }

                uniqueFilename = null;

                try {
                    uniqueFilename = uploadFileService.copy(foto);
                } catch (IOException var9) {
                    var9.printStackTrace();
                }

                flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");
                cliente.setFoto(uniqueFilename);
            }

            uniqueFilename = cliente.getId() != null ? "Cliente editado con éxito!" : "Cliente creado con éxito!";
            clienteService.save(cliente);
            status.setComplete();
            flash.addFlashAttribute("success", uniqueFilename);
            return "redirect:listar";
        }
    }

    @RequestMapping({"/eliminar/{id}"})
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        if (id > 0L) {
            Cliente cliente = clienteService.findOne(id);
            clienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito!");
            if (uploadFileService.delete(cliente.getFoto())) {
                flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito!");
            }
        }

        return "redirect:/listar";
    }
}
