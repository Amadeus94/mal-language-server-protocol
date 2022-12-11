package org.mal.ls.compiler.lib;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class MalLocation extends Location {

  public MalLocation(String uri, Range range) {
    super(uri, range);
  }

  public MalLocation(String uri, Position start, Position end) {
    super(uri, new Range(start, end));
  }

  public MalLocation(MalLocation location) {
    super(location.getUri(), location.getRange());
  }

  public String locationString() {
    return String.format("%s:%s", getFileName(), posString());
  }

  public String posString() {
    return String.format("<%s,%s>", getRange().getStart().getLine() + 1, getRange().getStart().getCharacter() + 1);
  }

  public MalLocation getLocation() {
    return this;
  }

  public Position getStart() {
    return this.getRange().getStart();
  }

  public Position getEnd() {
    return this.getRange().getEnd();
  }

  public String getFileName() {
    String[] arr = this.getUri().split("/");
    return arr[arr.length - 1];
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MalLocation)) {
      return false;
    }
    var other = (MalLocation) obj;
    return this.getUri().equals(other.getUri()) && this.getRange().equals(other.getRange());
  }

  @Override
  public String toString() {
    return locationString();
  }
}
