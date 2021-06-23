import java.util.Stack;

public class ScriptStack {
    Stack<ScriptCmd> commands;

    public ScriptStack() {
        commands = new Stack<>();
    }

    public boolean OP_DUP() {
        if (this.commands.size()<1) return false;
        this.commands.push(this.commands.peek());
        return true;
    }

    public boolean OP_HASH256() {
        if (this.commands.size()<1) return false;
        var element = this.commands.pop();
        var hashed = CryptoKit.hash256(element.value);
        commands.push(new ScriptCmd(element.type,hashed));
        return true;
    }

    public boolean OP_HASH160() {
        if (this.commands.size()<1) return false;
        var element = this.commands.pop();
        var hashed = CryptoKit.hash160(element.value);
        commands.push(new ScriptCmd(element.type,hashed));
        return true;

    }


}
