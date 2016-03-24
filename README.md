# Breath - Monitor de Frequência Respiratória (Respiratory Rate Monitor)

Google Play: https://play.google.com/store/apps/details?id=br.edu.ifpb.breath

>> PT/BR

- Descrição:

O aplicativo Breath foi desenvolvido para servir de interface ao monitoramento da frequência respiratória de pacientes e pode ser usado com qualquer circuito sensorial, que se comunique via Bluetooth e envie seus dados conforme o protocolo especificado a seguir.

- O Protocolo:

A aplicação conecta-se via Bluetooth com o sensor e espera então Strings que sigam o padrão JSON com os campos "frequency" do tipo inteiro e "amplitude" do tipo "inteiro". Como normalmente os sistemas de aquisição de dados possuem valores de ponto flutuante, o aplicativo utilizará o valor do campo amplitude dividido por 10, desta forma para exibir uma amplitude de 5 no gráfico do aplicativo deve-se enviar 50.

Exemplo de String a ser enviada: {"amplitude": 50, "frequency": 120}

- Para desenvolvedores/estudiosos:

O aplicativo Breath permite que os desenvolvedores ou estudiosos possam criar conjuntos sensoriais mais refinados para a medição da frequência respiratória. Em casos de dúvidas, entre em contato através de contact@felipeporge.com .

>> EN/US

- Description:

The Breath app was designed to interface to the monitoring of respiratory rate of patients and can be used with any sensory circuit that communicates via Bluetooth and send your data as specified Breath protocol, described bellow.

- Protocol:

The application connects via Bluetooth with the sensor and then waits String values that follow the JSON standard with the fields "frequency" (integer) and "amplitude" (integer). As usual data acquisition systems have floating point values, the application will use the value of the amplitude field divided by 10, so to display a range of 5 to the application chart, you should be sent 50.

Example of String to be sent: { "width": 50, "frequency": 120}

- For developers / scholars:

The Breath application allows developers or students tp create more refined sensorial systems for measuring respiratory rate. When in doubt, contact through contact@felipeporge.com.
