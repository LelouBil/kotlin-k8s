import com.examplek8s.models.SomeCRDSpec
import com.examplek8s.models.SomeCRDSpecPrune
import com.examplek8s.models.SomeCRDSpecSpec
import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.cfg.MapperBuilder
import tools.jackson.dataformat.yaml.YAMLFactory
import tools.jackson.dataformat.yaml.YAMLMapper

fun main() {
    val res = SomeCRDSpec(
        spec = SomeCRDSpecSpec(
            prune = SomeCRDSpecPrune(
                activeDeadlineSeconds = 5
            )
        )
    )

    val mapper = YAMLMapper.builder()
        .changeDefaultPropertyInclusion({ it.withValueInclusion(JsonInclude.Include.NON_NULL)})
        .build()


    val writeValueAsString = mapper.writeValueAsString(res)
    println(writeValueAsString)
}
