package net.mehdi.springbatch.mappers;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class IctEncoursBrutMapper {

    private ModelMapper modelMapper = new ModelMapper();

    public IctEncoursBrutDto entityToDto(IctEncoursBrut entity){
        IctEncoursBrutDto map = modelMapper.map(entity, IctEncoursBrutDto.class);
        return map;
    }

    public IctEncoursBrut dtoToEntity(IctEncoursBrutDto dto){
        IctEncoursBrut map = modelMapper.map(dto, IctEncoursBrut.class);
        return map;
    }
}






//import org.mapstruct.Mapper;
//import net.mehdi.springbatch.dto.IctEncoursBrutDto;
//import net.mehdi.springbatch.entities.IctEncoursBrut;

//@Mapper(componentModel = "spring")
//public interface IctEncoursBrutMapper {
//    IctEncoursBrutDto entityToDto(IctEncoursBrut entity);
//    IctEncoursBrut dtoToEntity(IctEncoursBrutDto dto);
//}
