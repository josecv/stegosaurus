require 'java'
require './target/stegosaurus-0.0.1-SNAPSHOT.jar'
require '/home/joe/.m2/repository/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar'

class ConcreteCoder < com.stegosaurus.steganographers.coders.JPEGCoder
  def close()
    print "Yay, closed!"
  end
  def loadWorkingSet()
    com.stegosaurus.steganographers.coders.JPEGCoder.
      instance_method(:loadWorkingSet).bind(self).call
  end
end

def main()
  print "Starting up\n"
  img = java.io.FileInputStream.new "./junk/test2.jpg"
  coder = ConcreteCoder.new img
  coder.loadWorkingSet()
  begin
    coder.loadScan()
  rescue => e
    print e
    print "\n"
    print e.backtrace.join("\n") + "\n"
  end
end

main()
