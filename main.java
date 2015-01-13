//
// Capstone testing HiJack in Console-App
//

import NuJack.*;

class main
{  
        public static void main(String args[])
        {
		//FakeAudioRecord fru = new FakeAudioRecord();
		//fru.loadFile();
		//fru.Print();

		//FakeSink fs = new FakeSink();

		//AudioReceiver aru = new AudioReceiver(fru);
		//aru.registerIncomingSink(fs);
		//aru.startAudioIO();

		/*
		SerialDecoder decoder = new SerialDecoder();
		decoder.start();
		decoder.print();
		*/
		
		Decoder dec = new Decoder();
		dec.start();
		dec.print();

		nByte _b = new nByte();
		_b.push(1);
		_b.push(0);
		_b.push(1);
          	//System.out.println(_b.intVal());
          	//System.out.println(_b.GetValue());



		//float num = 0.0f;
		int num = 1;
		num = num << 1;
		num = num + 1;
		// to push a 1:
		// 1) first shift left: 0000 0001 -> 0000 0010
		// 2) Then OR to 0001-0001:
		// 0000 0010  (AND with 1111 1111)
		// 1111 1111 & 0000 0010 -> 0000 0010
		// 0000 0001 
		//
		// 0100 0110 
		// 1) shift left then add 1

		//fs.Print();
          	System.out.println("------DONE------");
        }
}
